package net.ceedubs.ficus
package readers

import java.time.{ZoneId, ZonedDateTime}

import com.typesafe.config.ConfigFactory

import Ficus.{toFicusConfig, isoZonedDateTimeReader}

class ISOZonedDateTimeReaderSpec extends Spec {
  def is = s2"""
  The ISOZonedDateTimeReader should
    read a ZonedDateTime in ISO format $readZonedDateTime
  """

  def readZonedDateTime = {
    val cfg      = ConfigFactory.parseString(s"""
         | foo {
         |    date = "2016-02-28T11:46:26.896+01:00[Europe/Berlin]"
         | }
       """.stripMargin)
    val date     = cfg.as[ZonedDateTime]("foo.date")
    val expected = ZonedDateTime.of(
      2016,
      2,
      28,
      11,
      46,
      26,
      896000000,
      ZoneId.of("Europe/Berlin")
    )
    date should_== expected
  }
}
