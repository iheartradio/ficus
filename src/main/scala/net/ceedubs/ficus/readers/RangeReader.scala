package net.ceedubs.ficus.readers

import com.typesafe.config.{Config, ConfigException}

import scala.collection.immutable.Range

trait RangeReader {

  /** A reader for for a scala.collection.immutable.Range.
    * Example of values: "1 to 20", "0 to 20 step 2"
    */
  implicit def rangeReader: ValueReader[Range] = new ValueReader[Range] {
    def read(config: Config, path: String): Range = {
      val configString = config.getString(path)

      configString.split(" ").toList match {
        case from :: "to" :: to :: Nil                   => Range(parseInt(config, path, from), parseInt(config, path, to))
        case from :: "to" :: to :: "step" :: step :: Nil =>
          Range(parseInt(config, path, from), parseInt(config, path, to), parseInt(config, path, step))
        case _                                           => throw new ConfigException.BadValue(path, s"Can not parse `$configString` into Range")
      }
    }
  }

  private def parseInt(config: Config, path: String, stringValue: String): Int =
    try stringValue.toInt
    catch {
      case e: NumberFormatException =>
        throw new ConfigException.WrongType(config.origin(), path, "Int", "String", e)
    }
}
