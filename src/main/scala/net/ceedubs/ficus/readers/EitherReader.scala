package net.ceedubs.ficus.readers
import com.typesafe.config.{Config, ConfigException}

trait EitherReader {
  implicit def eitherReader[L, R](implicit
      lReader: ValueReader[L],
      rReader: ValueReader[R]
  ): ValueReader[Either[L, R]] =
    new ValueReader[Either[L, R]] {

      /** Reads the value at the path `path` in the Config */
      override def read(config: Config, path: String): Either[L, R] =
        TryReader
          .tryValueReader(rReader)
          .read(config, path)
          .map(Right(_))
          .recover { case _: ConfigException =>
            Left(lReader.read(config, path))
          }
          .get
    }
}

object EitherReader extends EitherReader
