package net.ceedubs.ficus.readers

import java.time.LocalDate

import com.typesafe.config.Config

trait LocalDateReader {
  implicit val localDateReader: ValueReader[LocalDate] = new ValueReader[LocalDate] {
    override def read(config: Config, path: String): LocalDate =
      LocalDate.parse(config.getString(path))
  }
}

object LocalDateReader extends LocalDateReader
