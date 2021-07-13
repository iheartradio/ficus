package net.ceedubs.ficus.readers

import java.net.URL

import com.typesafe.config.ConfigException.WrongType
import com.typesafe.config.ConfigFactory
import net.ceedubs.ficus.Spec
import org.specs2.matcher.MatchResult

class URLReaderSpec extends Spec with URLReader with TryReader {
  def is = s2"""
  The URL value reader should
    read a valid URL $readValidURL
    detect wrong type on malformed URL (with an unsupported protocol) $readMalformedURL
  """

  def readValidURL: MatchResult[URL] = {
    val url = """https://www.google.com"""
    val cfg = ConfigFactory.parseString(s"myValue = ${"\"" + url + "\""}")
    javaURLReader.read(cfg, "myValue") must beEqualTo(new URL(url))
  }

  def readMalformedURL: MatchResult[Any] = {
    val malformedUrl = """foo://bar.com"""
    val cfg          = ConfigFactory.parseString(s"myValue = ${"\"" + malformedUrl + "\""}")
    javaURLReader.read(cfg, "myValue") must throwA[WrongType]
  }
}
