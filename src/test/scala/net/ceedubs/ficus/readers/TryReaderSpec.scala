package net.ceedubs.ficus.readers

import com.typesafe.config.{ConfigException, Config, ConfigFactory}
import net.ceedubs.ficus.Spec
import org.scalacheck.Prop
import scala.util.Failure

class TryReaderSpec extends Spec with TryReader with AnyValReaders {
  def is = s2"""
  A try value reader should
    return a success when a value can be read $successWhenPresent
    return a failure when a value cannot be read $cannotBeRead
    handle an unexpected exception type $unexpectedExceptionType
    handle an unexpected exception $unexpectedException
  """

  def successWhenPresent = prop { (i: Int) =>
    val cfg = ConfigFactory.parseString(s"myValue = $i")
    tryValueReader[Int].read(cfg, "myValue") must beSuccessfulTry[Int].withValue(i)
  }

  def cannotBeRead = {
    val cfg = ConfigFactory.parseString("myValue = true")
    tryValueReader[Boolean].read(cfg, "wrongKey") must beFailedTry[Boolean].withThrowable[ConfigException.Missing]
  }

  def unexpectedExceptionType = {
    val cfg                                             = ConfigFactory.parseString("myValue = true")
    implicit val stringValueReader: ValueReader[String] = new ValueReader[String] {
      def read(config: Config, path: String): String = throw new NullPointerException("oops")
    }
    tryValueReader[String].read(cfg, "myValue") must beFailedTry[String].withThrowable[NullPointerException]("oops")
  }

  def unexpectedException = prop { (up: Throwable) =>
    val cfg                                             = ConfigFactory.parseString("myValue = true")
    implicit val stringValueReader: ValueReader[String] = new ValueReader[String] {
      def read(config: Config, path: String): String = throw up
    }
    val expectedMessage                                 = Option(up).map(_.getMessage).orNull
    tryValueReader[String].read(cfg, "myValue") must beFailedTry[String] and
      (up.getMessage == expectedMessage must beTrue)
  }
}
