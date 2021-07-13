package net.ceedubs.ficus

import com.typesafe.config.Config
import net.ceedubs.ficus.readers.{AllValueReaderInstances, ValueReader}
import scala.language.implicitConversions

trait FicusConfig {
  def config: Config

  def as[A](path: String)(implicit reader: ValueReader[A]): A = reader.read(config, path)

  def as[A](implicit reader: ValueReader[A]): A = as(".")

  def getAs[A](path: String)(implicit reader: ValueReader[Option[A]]): Option[A] = reader.read(config, path)

  def getOrElse[A](path: String, default: => A)(implicit reader: ValueReader[Option[A]]): A =
    getAs[A](path).getOrElse(default)

  def apply[A](key: ConfigKey[A])(implicit reader: ValueReader[A]): A = as[A](key.path)
}

final case class SimpleFicusConfig(config: Config) extends FicusConfig

@deprecated(
  "For implicits, use Ficus._ instead of FicusConfig._. Separately use ArbitraryTypeReader._ for macro-based derived reader instances. See https://github.com/ceedubs/ficus/issues/5",
  since = "1.0.1/1.1.1"
)
object FicusConfig extends AllValueReaderInstances {
  implicit def toFicusConfig(config: Config): FicusConfig = SimpleFicusConfig(config)
}
