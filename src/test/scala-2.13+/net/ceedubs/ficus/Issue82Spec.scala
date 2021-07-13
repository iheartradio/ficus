package net.ceedubs.ficus

import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import com.typesafe.config._
import org.specs2.mutable.Specification

class Issue82Spec extends Specification {
  "Ficus config" should {
    "not throw `java.lang.ClassCastException`" in {
      case class TestSettings(val `foo-bar`: Long, `foo`: String)
      val config = ConfigFactory.parseString("""{ foo-bar: 3, foo: "4" }""")
      config.as[TestSettings] must not(throwA[java.lang.ClassCastException])
    }

    """should not assign "foo-bar" to "foo"""" in {
      case class TestSettings(val `foo-bar`: String, `foo`: String)
      val config   = ConfigFactory.parseString("""{ foo-bar: "foo-bar", foo: "foo" }""")
      val settings = config.as[TestSettings]
      (settings.`foo-bar` mustEqual "foo-bar") and (settings.`foo` mustEqual "foo")
    }
  }
}
