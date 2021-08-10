package net.ceedubs.ficus.readers

import com.typesafe.config.Config

import java.time.temporal.ChronoUnit

trait ChronoUnitReader {
  implicit val chronoUnitReader: ValueReader[ChronoUnit] = new ValueReader[ChronoUnit] {

    /** Reads the value at the path `path` in the Config */
    override def read(config: Config, path: String): ChronoUnit =
      ChronoUnit.valueOf(config.getString(path).toUpperCase)
  }
}

object ChronoUnitReader extends ChronoUnitReader
