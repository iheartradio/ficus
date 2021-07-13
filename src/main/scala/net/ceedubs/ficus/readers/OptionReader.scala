package net.ceedubs.ficus.readers

import com.typesafe.config.Config

trait OptionReader {
  implicit def optionValueReader[A](implicit valueReader: ValueReader[A]): ValueReader[Option[A]] =
    new ValueReader[Option[A]] {
      def read(config: Config, path: String): Option[A] =
        if (config.hasPath(path)) {
          Some(valueReader.read(config, path))
        } else {
          None
        }
    }
}

object OptionReader extends OptionReader
