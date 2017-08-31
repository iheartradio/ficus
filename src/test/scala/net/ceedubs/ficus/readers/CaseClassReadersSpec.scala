package net.ceedubs.ficus
package readers

import com.typesafe.config.ConfigFactory
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import ConfigSerializerOps._

object CaseClassReadersSpec {
  case class SimpleCaseClass(bool: Boolean)
  case class MultipleFields(string: String, long: Long)
  case class WithOption(option: Option[String])
  case class WithNestedCaseClass(simple: SimpleCaseClass)
  case class ValueClass(int: Int) extends AnyVal
  case class WithNestedValueClass(valueClass: ValueClass)
  case class WithDefault(string: String = "bar")
  case class Foo(bool: Boolean, intOpt: Option[Int], withNestedCaseClass: WithNestedCaseClass,
                 withNestedValueClass: WithNestedValueClass)
}

class CaseClassReadersSpec extends Spec { def is = s2"""
  A case class reader should
    be able to be used implicitly $useImplicitly
    hydrate a simple case class $hydrateSimpleCaseClass
    hydrate a case class with multiple fields $multipleFields
    use another implicit value reader for a field $withOptionField
    read a nested case class $withNestedCaseClass
    read a top-level case class $withTopLevelCaseClass
    read a top-level case class with nested case class $withTopLevelCaseClassWithNestedCaseClass
    read a top-level value class $topLevelValueClass
    read a nested value class $nestedValueClass
    fall back to a default value $fallbackToDefault
    do a combination of these things $combination
  """

  import CaseClassReadersSpec._

  def useImplicitly = {
    val cfg = ConfigFactory.parseString("simple { bool = false }")
    cfg.as[SimpleCaseClass]("simple") must_== SimpleCaseClass(bool = false)
  }

  def hydrateSimpleCaseClass = prop { bool: Boolean =>
    val cfg = ConfigFactory.parseString(s"simple { bool = $bool }")
    cfg.as[SimpleCaseClass]("simple") must_== SimpleCaseClass(bool = bool)
  }

  def multipleFields = prop { (foo: String, long: Long) =>
    val cfg = ConfigFactory.parseString(
      s"""
        |multipleFields {
        |  string = ${foo.asConfigValue}
        |  long = $long
        |}
      """.stripMargin)
    cfg.as[MultipleFields]("multipleFields") must_== MultipleFields(string = foo, long = long)
  }

  def withOptionField = prop { s: String =>
    val cfg = ConfigFactory.parseString(s"""withOption { option = ${s.asConfigValue} }""")
    cfg.as[WithOption]("withOption") must_== WithOption(Some(s))
  }

  def withNestedCaseClass = prop { bool: Boolean =>
    val cfg = ConfigFactory.parseString(
      s"""
        |withNested {
        |  simple {
        |    bool = $bool
        |  }
        |}
      """.stripMargin)
    cfg.as[WithNestedCaseClass]("withNested") must_== WithNestedCaseClass(
      simple = SimpleCaseClass(bool = bool))
  }

  def withTopLevelCaseClass = prop{ bool : Boolean =>
    val cfg = ConfigFactory.parseString(s"{bool = $bool}")
    cfg.as[SimpleCaseClass] must_== SimpleCaseClass(bool = bool)
  }

  def withTopLevelCaseClassWithNestedCaseClass = prop{bool : Boolean =>
    val cfg = ConfigFactory.parseString(
      s"""
         |  simple {
         |    bool = $bool
         |  }
      """.stripMargin)
    cfg.as[WithNestedCaseClass] must_== WithNestedCaseClass(
      simple = SimpleCaseClass(bool = bool))
  }

  def topLevelValueClass = prop { int: Int =>
    val cfg = ConfigFactory.parseString(s"valueClass { int = $int }")
    cfg.as[ValueClass]("valueClass") must_== ValueClass(int)
  }

  def nestedValueClass = prop { int: Int =>
    val cfg = ConfigFactory.parseString(
      s"""
        |withNestedValueClass {
        |  valueClass {
        |    int = $int
        |  }
        |}
      """.stripMargin)
    cfg.as[WithNestedValueClass]("withNestedValueClass") must_== WithNestedValueClass(
      valueClass = ValueClass(int = int))
  }

  def fallbackToDefault = {
    val cfg = ConfigFactory.parseString("""withDefault { }""")
    cfg.as[WithDefault]("withDefault") must_== WithDefault()
  }

  def combination = prop { (fooBool: Boolean, simpleBool: Boolean, valueClassInt: Int) =>
    val cfg = ConfigFactory.parseString(
      s"""
        |foo {
        |  bool = $fooBool
        |  withNestedCaseClass {
        |    simple {
        |      bool = $simpleBool
        |    }
        |  }
        |  withNestedValueClass = {
        |    valueClass {
        |      int = $valueClassInt
        |    }
        |  }
        |}
      """.stripMargin)
    cfg.as[Foo]("foo") must_== Foo(
      bool = fooBool,
      intOpt = None,
      withNestedCaseClass = WithNestedCaseClass(simple = SimpleCaseClass(bool = simpleBool)),
      withNestedValueClass = WithNestedValueClass(ValueClass(int = valueClassInt))
    )
  }

}
