package net.ceedubs.ficus.readers

import java.net.{URI, URISyntaxException}

import com.typesafe.config.{Config, ConfigException}

trait URIReaders {
  implicit val javaURIReader: ValueReader[URI] = new ValueReader[URI] {
    def read(config: Config, path: String): URI = {
      val s = config.getString(path)
      try new URI(s)
      catch {
        case e: URISyntaxException =>
          throw new ConfigException.WrongType(config.origin(), path, "java.net.URI", "String", e)
      }
    }
  }

}

object URIReaders extends URIReaders
