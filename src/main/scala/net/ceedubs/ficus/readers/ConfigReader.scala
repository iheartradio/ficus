package net.ceedubs.ficus.readers

import com.typesafe.config.Config
import net.ceedubs.ficus.{SimpleFicusConfig, FicusConfig}

trait ConfigReader {
  implicit val configValueReader: ValueReader[Config] = new ValueReader[Config] {
    def read(config: Config, path: String): Config = if (path == ".") config else config.getConfig(path)
  }

  implicit val ficusConfigValueReader: ValueReader[FicusConfig] = configValueReader.map(SimpleFicusConfig)
}

object ConfigReader extends ConfigReader
