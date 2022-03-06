package net.ceedubs.ficus
package readers

import com.typesafe.config.ConfigFactory
import ConfigSerializerOps._
import shapeless.test.illTyped

class ArbitraryTypeReaderSpec extends Spec {
  def is = s2"""
  An arbitrary type reader should
    instantiate with a single-param apply method $instantiateSingleParamApply
    instantiate with a single-param apply method from config itself $instantiateSingleParamApplyFromSelf
    instantiate with no apply method but a single constructor with a single param $instantiateSingleParamConstructor
    instantiate with a multi-param apply method $instantiateMultiParamApply
    instantiate with no apply method but a single constructor with multiple params $instantiateMultiParamConstructor
    instantiate with multiple apply methods if only one returns the correct type $multipleApply
    instantiate with primary constructor when no apply methods and multiple constructors $multipleConstructors
    use another implicit value reader for a field $withOptionField
    fall back to a default value on an apply method $fallBackToApplyMethodDefaultValue
    fall back to default values on an apply method if base key isn't in config $fallBackToApplyMethodDefaultValueNoKey
    fall back to a default value on a constructor arg $fallBackToConstructorDefaultValue
    fall back to a default values on a constructor if base key isn't in config $fallBackToConstructorDefaultValueNoKey
    ignore a default value on an apply method if a value is in config $ignoreApplyParamDefault
    ignore a default value in a constructor if a value is in config $ignoreConstructorParamDefault
    allow overriding of option reader for default values $overrideOptionReaderForDefault
    not choose between multiple Java constructors $notChooseBetweenJavaConstructors
    not be prioritized over a Reader defined in a type's companion object (when Ficus._ is imported) $notTrumpCompanionReader
    use name mapper $useNameMapper
  """

  import ArbitraryTypeReaderSpec._

  def instantiateSingleParamApply = prop { (foo2: String) =>
    import Ficus.stringValueReader
    import ArbitraryTypeReader._
    val cfg                                = ConfigFactory.parseString(s"simple { foo2 = ${foo2.asConfigValue} }")
    val instance: WithSimpleCompanionApply =
      arbitraryTypeValueReader[WithSimpleCompanionApply].value.read(cfg, "simple")
    instance.foo must_== foo2
  }

  def instantiateSingleParamApplyFromSelf = prop { (foo2: String) =>
    import Ficus.stringValueReader
    import ArbitraryTypeReader._
    val cfg                                = ConfigFactory.parseString(s"simple { foo2 = ${foo2.asConfigValue} }")
    val instance: WithSimpleCompanionApply =
      arbitraryTypeValueReader[WithSimpleCompanionApply].value.read(cfg.getConfig("simple"), ".")
    instance.foo must_== foo2
  }

  def instantiateSingleParamConstructor = prop { (foo: String) =>
    import Ficus.stringValueReader
    import ArbitraryTypeReader._
    val cfg                            = ConfigFactory.parseString(s"singleParam { foo = ${foo.asConfigValue} }")
    val instance: ClassWithSingleParam = arbitraryTypeValueReader[ClassWithSingleParam].value.read(cfg, "singleParam")
    instance.getFoo must_== foo
  }

  def instantiateMultiParamApply = prop { (foo: String, bar: Int) =>
    import Ficus.{intValueReader, stringValueReader}
    import ArbitraryTypeReader._
    val cfg                               = ConfigFactory.parseString(s"""
        |multi {
        |  foo = ${foo.asConfigValue}
        |  bar = $bar
        |}""".stripMargin)
    val instance: WithMultiCompanionApply = arbitraryTypeValueReader[WithMultiCompanionApply].value.read(cfg, "multi")
    (instance.foo must_== foo) and (instance.bar must_== bar)
  }

  def instantiateMultiParamConstructor = prop { (foo: String, bar: Int) =>
    import Ficus.{intValueReader, stringValueReader}
    import ArbitraryTypeReader._
    val cfg                               = ConfigFactory.parseString(s"""
        |multi {
        |  foo = ${foo.asConfigValue}
        |  bar = $bar
        |}""".stripMargin)
    val instance: ClassWithMultipleParams = arbitraryTypeValueReader[ClassWithMultipleParams].value.read(cfg, "multi")
    (instance.foo must_== foo) and (instance.bar must_== bar)
  }

  def multipleApply = prop { (foo: String) =>
    import Ficus.stringValueReader
    import ArbitraryTypeReader._
    val cfg                                = ConfigFactory.parseString(s"withMultipleApply { foo = ${foo.asConfigValue} }")
    val instance: WithMultipleApplyMethods =
      arbitraryTypeValueReader[WithMultipleApplyMethods].value.read(cfg, "withMultipleApply")
    instance.foo must_== foo
  }

  def multipleConstructors = prop { (foo: String) =>
    import Ficus.stringValueReader
    import ArbitraryTypeReader._
    val cfg                                     = ConfigFactory.parseString(s"withMultipleConstructors { foo = ${foo.asConfigValue} }")
    val instance: ClassWithMultipleConstructors =
      arbitraryTypeValueReader[ClassWithMultipleConstructors].value.read(cfg, "withMultipleConstructors")
    instance.foo must_== foo
  }

  def fallBackToApplyMethodDefaultValue = {
    import Ficus.{optionValueReader, stringValueReader}
    import ArbitraryTypeReader._
    val cfg = ConfigFactory.parseString("withDefault { }")
    arbitraryTypeValueReader[WithDefault].value.read(cfg, "withDefault").foo must_== "defaultFoo"
  }

  def fallBackToApplyMethodDefaultValueNoKey = {
    import Ficus.{optionValueReader, stringValueReader}
    import ArbitraryTypeReader._
    val cfg = ConfigFactory.parseString("")
    arbitraryTypeValueReader[WithDefault].value.read(cfg, "withDefault").foo must_== "defaultFoo"
  }

  def fallBackToConstructorDefaultValue = {
    import Ficus.{optionValueReader, stringValueReader}
    import ArbitraryTypeReader._
    val cfg = ConfigFactory.parseString("withDefault { }")
    arbitraryTypeValueReader[ClassWithDefault].value.read(cfg, "withDefault").foo must_== "defaultFoo"
  }

  def fallBackToConstructorDefaultValueNoKey = {
    import Ficus.{optionValueReader, stringValueReader}
    import ArbitraryTypeReader._
    val cfg = ConfigFactory.parseString("")
    arbitraryTypeValueReader[ClassWithDefault].value.read(cfg, "withDefault").foo must_== "defaultFoo"
  }

  def withOptionField = {
    import Ficus.{optionValueReader, stringValueReader}
    import ArbitraryTypeReader._
    val cfg = ConfigFactory.parseString("""withOption { option = "here" }""")
    arbitraryTypeValueReader[WithOption].value.read(cfg, "withOption").option must_== Some("here")
  }

  def ignoreApplyParamDefault = prop { (foo: String) =>
    import Ficus.{optionValueReader, stringValueReader}
    import ArbitraryTypeReader._
    val cfg = ConfigFactory.parseString(s"withDefault { foo = ${foo.asConfigValue} }")
    arbitraryTypeValueReader[WithDefault].value.read(cfg, "withDefault").foo must_== foo
  }

  def ignoreConstructorParamDefault = prop { (foo: String) =>
    import Ficus.{optionValueReader, stringValueReader}
    import ArbitraryTypeReader._
    val cfg = ConfigFactory.parseString(s"withDefault { foo = ${foo.asConfigValue} }")
    arbitraryTypeValueReader[ClassWithDefault].value.read(cfg, "withDefault").foo must_== foo
  }

  def overrideOptionReaderForDefault = {
    import ArbitraryTypeReader._
    implicit val stringOptionReader: ValueReader[Option[String]] = Ficus.stringValueReader map { s =>
      if (s.isEmpty) None else Some(s)
    }
    val cfg                                                      = ConfigFactory.parseString("""withDefault { foo = "" }""")
    arbitraryTypeValueReader[ClassWithDefault].value.read(cfg, "withDefault").foo must beEqualTo("defaultFoo")
  }

  def notChooseBetweenJavaConstructors = {
    illTyped("implicitly[ValueReader[String]]")
    illTyped("implicitly[ValueReader[Long]]")
    illTyped("implicitly[ValueReader[Int]]")
    illTyped("implicitly[ValueReader[Float]]")
    illTyped("implicitly[ValueReader[Double]]")
    illTyped("implicitly[ValueReader[Char]]")
    success // failure would result in compile error
  }

  def notTrumpCompanionReader = {
    import Ficus._
    val cfg = ConfigFactory.parseString("""withReaderInCompanion { foo = "bar" }""")
    WithReaderInCompanion("from-companion") ==== cfg.as[WithReaderInCompanion]("withReaderInCompanion")
  }

  def useNameMapper = prop { (foo: String) =>
    import Ficus.stringValueReader
    import ArbitraryTypeReader._
    implicit val nameMapper: NameMapper = new NameMapper {
      override def map(name: String): String = name.toUpperCase
    }

    val cfg                            = ConfigFactory.parseString(s"singleParam { FOO = ${foo.asConfigValue} }")
    val instance: ClassWithSingleParam = arbitraryTypeValueReader[ClassWithSingleParam].value.read(cfg, "singleParam")
    instance.getFoo must_== foo
  }
}

object ArbitraryTypeReaderSpec {
  trait WithSimpleCompanionApply {
    def foo: String
  }

  object WithSimpleCompanionApply {
    def apply(foo2: String): WithSimpleCompanionApply = new WithSimpleCompanionApply {
      val foo = foo2
    }
  }

  trait WithMultiCompanionApply {
    def foo: String
    def bar: Int
  }

  object WithMultiCompanionApply {
    def apply(foo: String, bar: Int): WithMultiCompanionApply = {
      val (_foo, _bar) = (foo, bar)
      new WithMultiCompanionApply {
        val foo = _foo
        val bar = _bar
      }
    }
  }

  trait WithDefault {
    def foo: String
  }

  object WithDefault {
    def apply(foo: String = "defaultFoo"): WithDefault = {
      val _foo = foo
      new WithDefault {
        val foo = _foo
      }
    }
  }

  trait WithOption {
    def option: Option[String]
  }

  object WithOption {
    def apply(option: Option[String]): WithOption = {
      val _option = option
      new WithOption {
        val option = _option
      }
    }
  }

  class ClassWithSingleParam(foo: String) {
    def getFoo = foo
  }

  class ClassWithMultipleParams(val foo: String, val bar: Int)

  class ClassWithDefault(val foo: String = "defaultFoo")

  trait WithMultipleApplyMethods {
    def foo: String
  }

  object WithMultipleApplyMethods {

    def apply(foo: Option[String]): Option[WithMultipleApplyMethods] = foo map { f =>
      new WithMultipleApplyMethods {
        def foo: String = f
      }
    }

    def apply(foo: String): WithMultipleApplyMethods = {
      val _foo = foo
      new WithMultipleApplyMethods {
        def foo: String = _foo
      }
    }
  }

  class ClassWithMultipleConstructors(val foo: String) {
    def this(fooInt: Int) = this(fooInt.toString)
  }

  case class WithReaderInCompanion(foo: String)

  object WithReaderInCompanion {
    implicit val reader: ValueReader[WithReaderInCompanion] =
      ValueReader.relative(_ => WithReaderInCompanion("from-companion"))
  }

}
