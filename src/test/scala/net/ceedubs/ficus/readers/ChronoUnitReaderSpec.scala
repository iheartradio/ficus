package net.ceedubs.ficus
package readers

import java.time.temporal.ChronoUnit

import com.typesafe.config.ConfigFactory
import Ficus.{chronoUnitReader, toFicusConfig}

class ChronoUnitReaderSpec extends Spec {
  def is = s2"""
  The ChronoUnitReader should
    read a ChronoUnit $readChronoUnit
    read a lower case ChronoUnit $readChronoUnitLowerCase
    read a half-days ChronoUnit $readChronoUnitHalfDaysDash
  """

  def readChronoUnit = {
    val cfg        = ConfigFactory.parseString(s"""
                                                | foo {
                                                |    chrono-unit = "MILLIS"
                                                | }
       """.stripMargin)
    val chronoUnit = cfg.as[ChronoUnit]("foo.chrono-unit")
    val expected   = ChronoUnit.MILLIS
    chronoUnit should_== expected
  }

  def readChronoUnitLowerCase = {
    val cfg        = ConfigFactory.parseString(s"""
                                                | foo {
                                                |    chrono-unit = "millis"
                                                | }
       """.stripMargin)
    val chronoUnit = cfg.as[ChronoUnit]("foo.chrono-unit")
    val expected   = ChronoUnit.MILLIS
    chronoUnit should_== expected
  }

  def readChronoUnitHalfDaysDash = {
    val cfg        = ConfigFactory.parseString(s"""
                                                  | foo {
                                                  |    chrono-unit = "half-days"
                                                  | }
       """.stripMargin)
    val chronoUnit = cfg.as[ChronoUnit]("foo.chrono-unit")
    val expected   = ChronoUnit.HALF_DAYS
    chronoUnit should_== expected
  }

}
