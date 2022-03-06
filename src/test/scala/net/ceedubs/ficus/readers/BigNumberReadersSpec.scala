package net.ceedubs.ficus.readers

import com.typesafe.config.ConfigException.WrongType
import com.typesafe.config.ConfigFactory
import net.ceedubs.ficus.Spec

class BigNumberReadersSpec extends Spec with BigNumberReaders {
  def is = s2"""
  The BigDecimal value reader should
    read a double $readDoubleAsBigDecimal
    read a long $readLongAsBigDecimal
    read an int $readIntAsBigDecimal
    read a bigInt $readBigIntAsBigDecimal
    read a bigDecimalAsString $readBigDecimalAsStringBigDecimal
    read a bigIntAsString $readBigIntAsStringBigDecimal
    detect wrong type on malformed BigDecimal $readMalformedBigDecimal

  The BigInt value reader should
    read an int $readIntAsBigInt
    read a long $readLongAsBigInt
    read a bigInt $readBigIntAsBigInt
    read a bigIntAsString $readBigIntAsStringBigInt
    detect wrong type on malformed BigInt $readMalformedBigInt
   """

  def readDoubleAsBigDecimal = prop { (d: Double) =>
    val cfg = ConfigFactory.parseString(s"myValue = $d")
    bigDecimalReader.read(cfg, "myValue") must beEqualTo(BigDecimal(d))
  }

  def readLongAsBigDecimal = prop { (l: Long) =>
    val cfg = ConfigFactory.parseString(s"myValue = $l")
    bigDecimalReader.read(cfg, "myValue") must beEqualTo(BigDecimal(l))
  }

  def readIntAsBigDecimal = prop { (i: Int) =>
    val cfg = ConfigFactory.parseString(s"myValue = $i")
    bigDecimalReader.read(cfg, "myValue") must beEqualTo(BigDecimal(i))
  }

  /*
   Due to differences with BigDecimal precision handling in scala 2.10, this
   test is temporarily disabled. The next test compares the string
   representation of the BigDecimal and serves as a test of the actual
   functionality provided by this library, which simply parses the number
   as a string and calls BigDecimal's apply method. The quality of that
   BigDecimal implementation is not the concern of this library.

   def readBigDecimal = prop{ b: BigDecimal =>
    scala.util.Try(BigDecimal(b.toString)).toOption.isDefined ==> {
      val cfg = ConfigFactory.parseString(s"myValue = $b")
      bigDecimalReader.read(cfg, "myValue") must beEqualTo(b)
    }
  }
   */

  def readBigDecimalAsStringBigDecimal = prop { (b: BigDecimal) =>
    scala.util.Try(BigDecimal(b.toString)).toOption.isDefined ==> {
      val cfg = ConfigFactory.parseString(s"myValue = ${b.toString}")
      bigDecimalReader.read(cfg, "myValue") must beEqualTo(BigDecimal(b.toString))
    }
  }

  def readBigIntAsStringBigDecimal = prop { (b: BigInt) =>
    scala.util.Try(BigDecimal(b.toString)).toOption.isDefined ==> {
      val cfg = ConfigFactory.parseString(s"myValue = ${b.toString}")
      bigDecimalReader.read(cfg, "myValue") must beEqualTo(BigDecimal(b.toString))
    }
  }

  def readMalformedBigDecimal = {
    val malformedBigDecimal = "foo"
    val cfg                 = ConfigFactory.parseString(s"myValue = ${"\"" + malformedBigDecimal + "\""}")
    bigDecimalReader.read(cfg, "myValue") must throwA[WrongType]
  }

  def readBigIntAsBigDecimal = prop { (b: BigInt) =>
    scala.util.Try(BigDecimal(b)).toOption.isDefined ==> {
      val cfg = ConfigFactory.parseString(s"myValue = $b")
      bigDecimalReader.read(cfg, "myValue") must beEqualTo(BigDecimal(b))
    }
  }

  def readIntAsBigInt = prop { (i: Int) =>
    val cfg = ConfigFactory.parseString(s"myValue = $i")
    bigIntReader.read(cfg, "myValue") must beEqualTo(BigInt(i))
  }

  def readLongAsBigInt = prop { (l: Long) =>
    val cfg = ConfigFactory.parseString(s"myValue = $l")
    bigIntReader.read(cfg, "myValue") must beEqualTo(BigInt(l))
  }

  def readBigIntAsBigInt = prop { (b: BigInt) =>
    scala.util.Try(BigInt(b.toString)).toOption.isDefined ==> {
      val cfg = ConfigFactory.parseString(s"myValue = $b")
      bigIntReader.read(cfg, "myValue") must beEqualTo(BigInt(b.toString))
    }
  }

  def readBigIntAsStringBigInt = prop { (b: BigInt) =>
    scala.util.Try(BigInt(b.toString)).toOption.isDefined ==> {
      val cfg = ConfigFactory.parseString(s"myValue = ${b.toString}")
      bigIntReader.read(cfg, "myValue") must beEqualTo(BigInt(b.toString))
    }
  }

  def readMalformedBigInt = {
    val malformedBigInt = "foo"
    val cfg             = ConfigFactory.parseString(s"myValue = ${"\"" + malformedBigInt + "\""}")
    bigIntReader.read(cfg, "myValue") must throwA[WrongType]
  }
}
