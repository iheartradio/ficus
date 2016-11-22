package net.ceedubs.ficus.readers

import java.time.Period

import com.typesafe.config.Config

trait PeriodReader {
  implicit val periodReader: ValueReader[Period] = new ValueReader[Period] {
    override def read(config: Config, path: String): Period = {
      Period.parse(config.getString(path))
    }
  }
}

object PeriodReader extends PeriodReader
