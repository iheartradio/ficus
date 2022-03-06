package net.ceedubs.ficus
package readers

import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._
import org.scalacheck.{Gen, Prop}

class DurationReadersSpec extends Spec with DurationReaders {
  def is = s2"""
  The finite duration reader should
    read a millisecond value ${readMillis(finiteDurationReader)}
    read a minute value ${readMinutes(finiteDurationReader)}
    read a days value into days ${readDaysUnit(finiteDurationReader)}

  The duration reader should
    read a millisecond value ${readMillis(durationReader)}
    read a minute value ${readMinutes(durationReader)}
    read a days value into days ${readDaysUnit(durationReader)}
    read positive infinite values $readPositiveInf
    read negative infinite values $readNegativeInf
  """

  def readMillis[T](reader: ValueReader[T]) = prop { (i: Int) =>
    val cfg = ConfigFactory.parseString(s"myValue = $i")
    reader.read(cfg, "myValue") must beEqualTo(i.millis)
  }

  def readMinutes[T](reader: ValueReader[T]) = Prop.forAll(Gen.choose(-1.5e8.toInt, 1.5e8.toInt)) { (i: Int) =>
    val cfg = ConfigFactory.parseString("myValue = \"" + i + " minutes\"")
    reader.read(cfg, "myValue") must_== i.minutes
  }

  def readDaysUnit[T](reader: ValueReader[T]) = Prop.forAll(Gen.choose(-106580, 106580)) { (i: Int) =>
    val str = i.toString + " day" + (if (i == 1) "" else "s")
    val cfg = ConfigFactory.parseString(s"""myValue = "$str" """)
    reader.read(cfg, "myValue").toString == str
  }

  def readPositiveInf = {
    val positiveInf = List("Inf", "PlusInf", "\"+Inf\"")
    positiveInf.forall { (s: String) =>
      val cfg = ConfigFactory.parseString(s"myValue = $s")
      durationReader.read(cfg, "myValue") == Duration.Inf
    }
  }

  def readNegativeInf = {
    val negativeInf = List("-Inf", "MinusInf")
    negativeInf.forall { (s: String) =>
      val cfg = ConfigFactory.parseString(s"myValue = $s")
      durationReader.read(cfg, "myValue") == Duration.MinusInf
    }
  }
}
