package net.ceedubs.ficus

import com.typesafe.config.{Config, ConfigFactory}
import Ficus.{ booleanValueReader, longValueReader, optionValueReader, stringValueReader, toFicusConfig }
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import net.ceedubs.ficus.readers.ValueReader
import ConfigSerializerOps._

object FicusConfigSpec{
  case class MultipleFields(string: String, long: Long)
}

class FicusConfigSpec extends Spec { def is = s2"""
  A Ficus config should
    be implicitly converted from a Typesafe config $implicitlyConverted
    read a value with a value reader $readAValue
    read a value with a composite value reader $readAValue
    get an existing value as a Some $getAsSome
    get a missing value as a None $getAsNone
    getOrElse an existing value as asked type $getOrElseFromConfig
    getOrElse a missing value with default value $getOrElseFromDefault
    getOrElse an existing value as asked type with customer reader $getOrElseFromConfigWithCustomValueReader
    accept a ConfigKey and return the appropriate type $acceptAConfigKey
  """

  import FicusConfigSpec._

  def implicitlyConverted = {
    val cfg = ConfigFactory.parseString("myValue = true")
    cfg.as[Boolean]("myValue") must beTrue
  }

  def readAValue = prop { b: Boolean =>
    val cfg = ConfigFactory.parseString(s"myValue = $b")
    cfg.as[Boolean]("myValue") must beEqualTo(b)
  }

  def multipleFields = prop { (foo: String, long: Long) =>
    val cfg = ConfigFactory.parseString(
      s"""
         |{
         |  string = ${foo.asConfigValue}
         |  long = $long
         |}
      """.stripMargin)
    val x: MultipleFields = cfg.as[MultipleFields]
    cfg.as[MultipleFields] must_== MultipleFields(string = foo, long = long)
  }

  def getAsSome = prop { b: Boolean =>
    val cfg = ConfigFactory.parseString(s"myValue = $b")
    cfg.getAs[Boolean]("myValue") must beSome(b)
  }

  def getAsNone = {
    val cfg = ConfigFactory.parseString("myValue = true")
    cfg.getAs[Boolean]("nonValue") must beNone
  }

  def getOrElseFromConfig = {
    val configString = "arealstring"
    val cfg = ConfigFactory.parseString(s"myValue = $configString")
    cfg.getOrElse("myValue", "notarealstring") must beEqualTo(configString)
  }

  def getOrElseFromDefault = {
    val cfg = ConfigFactory.parseString("myValue = arealstring")
    val default = "adefaultstring"
    cfg.getOrElse("nonValue", default) must beEqualTo(default)
  }

  def getOrElseFromConfigWithCustomValueReader = {
    val cfg = ConfigFactory.parseString("myValue = 124")
    val default = 23.toByte

    implicit val byteReader = new ValueReader[Byte]{
      def read(config: Config, path: String): Byte = config.getInt(path).toByte
    }

    cfg.getOrElse("myValue", default) must beEqualTo(124.toByte)
  }


  def acceptAConfigKey = prop { b: Boolean =>
    val cfg = ConfigFactory.parseString(s"myValue = $b")
    val key: ConfigKey[Boolean] = SimpleConfigKey("myValue")
    cfg(key) must beEqualTo(b)
  }
}
