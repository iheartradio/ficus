package net.ceedubs.ficus.readers

import com.typesafe.config.{ConfigException, ConfigFactory}
import net.ceedubs.ficus.Spec
import org.specs2.execute.Result

class RangeReaderSpec extends Spec with RangeReader {
  def is = s2"""
  The range reader should
    read a int range ${readIntRange(rangeReader)}
    read a int range with step ${readIntWithStepRange(rangeReader)}
    correctly handle wrong value in range ${correctlyHandleWrongValueInRange(rangeReader)}
    correctly handle wrong value ${correctlyHandleWrongValueInRange(rangeReader)}
  """

  def readIntRange[T](reader: ValueReader[T]) = prop { (from: Int, to: Int) =>
    val cfg = ConfigFactory.parseString(s"myValue = $from to $to")
    rangeEqualsTo(reader.read(cfg, "myValue"), Range(from, to))
  }

  def readIntWithStepRange[T](reader: ValueReader[T]) = prop { (from: Int, to: Int) =>
    val cfg = ConfigFactory.parseString(s"myValue = $from to $to step 10")
    rangeEqualsTo(reader.read(cfg, "myValue"), Range(from, to, 10))
  }

  def correctlyHandleWrongValueInRange[T](reader: ValueReader[T]) = {
    val cfg = ConfigFactory.parseString(s"myValue = 0 to wrongValue step 1")
    reader.read(cfg, "myValue") must throwA[ConfigException.WrongType]
  }

  def correctlyHandleWrongValue[T](reader: ValueReader[T]) = {
    val cfg = ConfigFactory.parseString(s"myValue = weird value")
    reader.read(cfg, "myValue") must throwA[ConfigException.BadValue]
  }

  private def rangeEqualsTo[T](actual: Any, expectedRange: Range): Result =
    actual match {
      case r: Range =>
        r.start must beEqualTo(expectedRange.start)
        r.end must beEqualTo(expectedRange.end)
        r.step must beEqualTo(expectedRange.step)
      case _        => failure(s"$actual is not range")
    }
}
