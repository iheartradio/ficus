package net.ceedubs.ficus.readers

import com.typesafe.config.ConfigValue
import com.typesafe.config.Config

trait ConfigValueReader {
  implicit val configValueValueReader: ValueReader[ConfigValue] = new ValueReader[ConfigValue] {
    override def read(config: Config, path: String): ConfigValue = config.getValue(path)
  }
}

object ConfigValueReader extends ConfigValueReader
