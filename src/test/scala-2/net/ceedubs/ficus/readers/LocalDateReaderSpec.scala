package net.ceedubs.ficus
package readers

import java.time.LocalDate
import com.typesafe.config.ConfigFactory
import Ficus.{toFicusConfig, localDateReader}

class LocalDateReaderSpec extends Spec {
  def is = s2"""
  The LocalDateReader should 
    read a LocalDate in ISO format without a time-zone: $readLocalDate
  """

  def readLocalDate = {
    val cfg       = ConfigFactory.parseString(s"""
         | foo {
         |    date = "2003-01-03"
         | }
       """.stripMargin)
    val localDate = cfg.as[LocalDate]("foo.date")
    val expected  = LocalDate.of(2003, 1, 3)
    localDate should_== expected
  }
}
