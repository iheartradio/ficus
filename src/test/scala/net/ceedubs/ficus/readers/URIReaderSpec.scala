package net.ceedubs.ficus.readers

import java.net.URI

import com.typesafe.config.ConfigException.WrongType
import com.typesafe.config.ConfigFactory
import net.ceedubs.ficus.Spec

class URIReaderSpec extends Spec with URIReaders {
  def is = s2"""
  The URI value reader should
    read a valid URI $readValidURI
    detect wrong type on malformed URI $readMalformedURI
  """

  def readValidURI = {
    val uri = """https://www.google.com"""
    val cfg = ConfigFactory.parseString(s"myValue = ${"\"" + uri + "\""}")
    javaURIReader.read(cfg, "myValue") must beEqualTo(new URI(uri))
  }

  def readMalformedURI = {
    val malformedUri = """foo://{bar}.com"""
    val cfg = ConfigFactory.parseString(s"myValue = ${"\"" + malformedUri + "\""}")
    javaURIReader.read(cfg, "myValue") must throwA[WrongType]
  }
}
