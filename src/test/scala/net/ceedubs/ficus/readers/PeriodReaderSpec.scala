package net.ceedubs.ficus
package readers

import java.time.Period
import com.typesafe.config.ConfigFactory
import Ficus.{toFicusConfig, periodReader}

class PeriodReaderSpec extends Spec {
  def is = s2"""
  The PeriodReader should 
    read a Period in ISO-8601 format $readPeriod
    read a negative Period $readNegativePeriod
  """

  def readPeriod = {
    val cfg      = ConfigFactory.parseString(s"""
         | foo {
         |    interval = "P1Y3M10D"
         | }
       """.stripMargin)
    val period   = cfg.as[Period]("foo.interval")
    val expected = Period.of(1, 3, 10)
    period should_== expected
  }

  def readNegativePeriod = {
    val cfg      = ConfigFactory.parseString(s"""
         | foo {
         |    interval = "P-1Y10M3D"
         | }
       """.stripMargin)
    val period   = cfg.as[Period]("foo.interval")
    val expected = Period.of(-1, 10, 3)
    period should_== expected
  }
}
