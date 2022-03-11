package net.ceedubs.ficus.readers

import scala.concurrent.duration.FiniteDuration
import com.typesafe.config.{Config, ConfigException}
import scala.concurrent.duration.{Duration, NANOSECONDS}
import scala.util.Try

trait DurationReaders {

  /** A reader for for a scala.concurrent.duration.FiniteDuration. This reader should be able to read any valid duration
   *  format as defined by the <a href="https://github.com/typesafehub/config/blob/master/HOCON.md">HOCON spec</a>. For
   *  example, it can read "15 minutes" or "1 day".
   */
  implicit def finiteDurationReader: ValueReader[FiniteDuration] = new ValueReader[FiniteDuration] {
    def read(config: Config, path: String): FiniteDuration = {
      val nanos = config.getDuration(path, NANOSECONDS)
      Duration.fromNanos(nanos)
    }
  }

  /** A reader for for a scala.concurrent.duration.Duration. This reader should be able to read any valid duration
   *  format as defined by the <a href="https://github.com/typesafehub/config/blob/master/HOCON.md">HOCON spec</a> and
   *  positive and negative infinite values supported by Duration's <a
   *  href="http://www.scala-lang.org/api/current/scala/
   *  concurrent/duration/Duration$.html#apply(s:String):scala.concurrent.duration.Duration">apply</a> method. For
   *  example, it can read "15 minutes", "1 day", "-Inf", or "PlusInf".
   */
  implicit def durationReader: ValueReader[Duration] = new ValueReader[Duration] {
    def read(config: Config, path: String): Duration =
      (Try {
        finiteDurationReader.read(config, path)
      } recover { case _: ConfigException.BadValue =>
        val nonFinite = config.getString(path)
        Duration(nonFinite)
      }).get
  }
}

object DurationReaders extends DurationReaders
