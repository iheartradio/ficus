package net.ceedubs.ficus.readers

import com.typesafe.config.Config
import macrocompat.bundle
import net.ceedubs.ficus.util.ReflectionUtils

import scala.language.experimental.macros
import scala.reflect.internal.{Definitions, StdNames, SymbolTable}
import scala.reflect.macros.blackbox

trait ArbitraryTypeReader {
  implicit def arbitraryTypeValueReader[T]: Generated[ValueReader[T]] =
    macro ArbitraryTypeReaderMacros.arbitraryTypeValueReader[T]
}

object ArbitraryTypeReader extends ArbitraryTypeReader

@bundle
class ArbitraryTypeReaderMacros(val c: blackbox.Context) extends ReflectionUtils {
  import c.universe._

  def arbitraryTypeValueReader[T: c.WeakTypeTag]: c.Expr[Generated[ValueReader[T]]] =
    reify {
      Generated(new ValueReader[T] {
        def read(config: Config, path: String): T = instantiateFromConfig[T](
          config = c.Expr[Config](Ident(TermName("config"))),
          path = c.Expr[String](Ident(TermName("path"))),
          mapper = c.Expr[NameMapper](q"""_root_.net.ceedubs.ficus.readers.NameMapper()""")
        ).splice
      })
    }

  def instantiateFromConfig[T: c.WeakTypeTag](
      config: c.Expr[Config],
      path: c.Expr[String],
      mapper: c.Expr[NameMapper]
  ): c.Expr[T] = {
    val returnType = c.weakTypeOf[T]

    def fail(reason: String) =
      c.abort(c.enclosingPosition, s"Cannot generate a config value reader for type $returnType, because $reason")

    val companionSymbol = returnType.typeSymbol.companion match {
      case NoSymbol => None
      case x        => Some(x)
    }

    val initMethod = instantiationMethod[T](fail)

    val instantiationArgs   = extractMethodArgsFromConfig[T](
      method = initMethod,
      companionObjectMaybe = companionSymbol,
      config = config,
      path = path,
      mapper = mapper,
      fail = fail
    )
    val instantiationObject = companionSymbol
      .filterNot(_ => initMethod.isConstructor)
      .map(Ident(_))
      .getOrElse(New(Ident(returnType.typeSymbol)))
    val instantiationCall   = Select(instantiationObject, initMethod.name)
    c.Expr[T](Apply(instantiationCall, instantiationArgs))
  }

  def extractMethodArgsFromConfig[T: c.WeakTypeTag](
      method: c.universe.MethodSymbol,
      companionObjectMaybe: Option[c.Symbol],
      config: c.Expr[Config],
      path: c.Expr[String],
      mapper: c.Expr[NameMapper],
      fail: String => Nothing
  ): List[c.Tree] = {
    val decodedMethodName = method.name.decodedName.toString

    if (!method.isPublic) fail(s"'$decodedMethodName' method is not public")

    method.paramLists.head.zipWithIndex map { case (param, index) =>
      val name             = param.name.decodedName.toString
      val key              = q"""if ($path == ".") $mapper.map($name) else $path + "." + $mapper.map($name)"""
      val returnType: Type = param.typeSignatureIn(c.weakTypeOf[T])

      companionObjectMaybe.filter(_ => param.asTerm.isParamWithDefault) map { companionObject =>
        val optionType       = appliedType(weakTypeOf[Option[_]].typeConstructor, List(returnType))
        val optionReaderType = appliedType(weakTypeOf[ValueReader[_]].typeConstructor, List(optionType))
        val optionReader     = c.inferImplicitValue(optionReaderType, silent = true) match {
          case EmptyTree =>
            fail(
              s"an implicit value reader of type $optionReaderType must be in scope to read parameter '$name' on '$decodedMethodName' method since '$name' has a default value"
            )
          case x         => x
        }
        val argValueMaybe    = q"$optionReader.read($config, $key)"
        Apply(
          Select(argValueMaybe, TermName("getOrElse")),
          List {
            // fall back to default value for param
            val u      = c.universe.asInstanceOf[Definitions with SymbolTable with StdNames]
            val getter = u.nme.defaultGetterName(u.newTermName(decodedMethodName), index + 1)
            Select(Ident(companionObject), TermName(getter.encoded))
          }
        )
      } getOrElse {
        val readerType = appliedType(weakTypeOf[ValueReader[_]].typeConstructor, List(returnType))
        val reader     = c.inferImplicitValue(readerType, silent = true) match {
          case EmptyTree =>
            fail(
              s"an implicit value reader of type $readerType must be in scope to read parameter '$name' on '$decodedMethodName' method"
            )
          case x         => x
        }
        q"$reader.read($config, $key)"
      }
    }
  }
}
