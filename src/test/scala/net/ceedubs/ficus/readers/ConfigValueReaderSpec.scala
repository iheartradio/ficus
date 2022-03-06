package net.ceedubs.ficus.readers

import com.typesafe.config.ConfigFactory
import net.ceedubs.ficus.Spec
import com.typesafe.config.ConfigValueType
import net.ceedubs.ficus.ConfigSerializerOps._

class ConfigValueReaderSpec extends Spec with ConfigValueReader {
  def is = s2"""
  The ConfigValue value reader should
    read a boolean $readBoolean
    read an int $readInt
    read a double $readDouble
    read a string $readString
    read an object $readObject
  """

  def readBoolean = prop { (b: Boolean) =>
    val cfg  = ConfigFactory.parseString(s"myValue = $b")
    val read = configValueValueReader.read(cfg, "myValue")
    read.valueType must beEqualTo(ConfigValueType.BOOLEAN)
    read.unwrapped() must beEqualTo(b)
  }

  def readInt = prop { (i: Int) =>
    val cfg  = ConfigFactory.parseString(s"myValue = $i")
    val read = configValueValueReader.read(cfg, "myValue")
    read.valueType must beEqualTo(ConfigValueType.NUMBER)
    read.unwrapped() must beEqualTo(int2Integer(i))
  }

  def readDouble = prop { (d: Double) =>
    val cfg  = ConfigFactory.parseString(s"myValue = $d")
    val read = configValueValueReader.read(cfg, "myValue")
    read.valueType must beEqualTo(ConfigValueType.NUMBER)
    read.unwrapped() must beEqualTo(double2Double(d))
  }

  def readString = prop { (s: String) =>
    val cfg  = ConfigFactory.parseString(s"myValue = ${s.asConfigValue}")
    val read = configValueValueReader.read(cfg, "myValue")
    read.valueType must beEqualTo(ConfigValueType.STRING)
    read.unwrapped() must beEqualTo(s)
  }

  def readObject = prop { (i: Int) =>
    val cfg  = ConfigFactory.parseString(s"myValue = { i = $i }")
    val read = configValueValueReader.read(cfg, "myValue")
    read.valueType must beEqualTo(ConfigValueType.OBJECT)
    read.unwrapped() must beEqualTo(cfg.getValue("myValue").unwrapped())
  }

  def readList = prop { (i: Int) =>
    val cfg  = ConfigFactory.parseString(s"myValue = [ $i ]")
    val read = configValueValueReader.read(cfg, "myValue")
    read.valueType must beEqualTo(ConfigValueType.LIST)
    read.unwrapped() must beEqualTo(cfg.getValue("myValue").unwrapped())
  }
}
