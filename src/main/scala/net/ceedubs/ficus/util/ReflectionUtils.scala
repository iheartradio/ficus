package net.ceedubs.ficus.util

import macrocompat.bundle
import scala.reflect.macros.blackbox

@bundle
trait ReflectionUtils {
  val c: blackbox.Context

  import c.universe._

  def instantiationMethod[T: c.WeakTypeTag](fail: String => Nothing): c.universe.MethodSymbol = {

    val returnType = c.weakTypeOf[T]

    val returnTypeTypeArgs = returnType match {
      case TypeRef(_, _, args) => args
      case _                   => Nil
    }

    if (returnTypeTypeArgs.nonEmpty)
      fail(
        s"value readers cannot be auto-generated for types with type parameters. Consider defining your own ValueReader[$returnType]"
      )

    val companionSymbol = returnType.typeSymbol.companion match {
      case NoSymbol => None
      case x        => Some(x)
    }

    val applyMethods = companionSymbol.toList.flatMap(_.typeSignatureIn(returnType).members collect {
      case m: MethodSymbol if m.name.decodedName.toString == "apply" && m.returnType <:< returnType => m
    })

    val applyMethod = applyMethods match {
      case Nil           => None
      case (head :: Nil) => Some(head)
      case _             => fail(s"its companion object has multiple apply methods that return type $returnType")
    }

    applyMethod getOrElse {
      val primaryConstructor = returnType.decl(termNames.CONSTRUCTOR) match {
        case t: TermSymbol =>
          val constructors            = t.alternatives collect {
            case m: MethodSymbol if m.isConstructor => m
          }
          val primaryScalaConstructor = constructors.find(m => m.isPrimaryConstructor && !m.isJava)
          primaryScalaConstructor orElse {
            if (constructors.length == 1) constructors.headOption else None
          }
        case _             => None
      }
      primaryConstructor getOrElse {
        fail(
          s"it has no apply method in a companion object that returns type $returnType, and it doesn't have a primary constructor"
        )
      }
    }
  }

}
