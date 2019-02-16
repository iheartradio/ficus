package net.ceedubs.ficus.readers

import java.net.InetSocketAddress

import com.typesafe.config.ConfigException.WrongType
import com.typesafe.config.ConfigFactory
import net.ceedubs.ficus.Spec

class InetSocketAddressReadersSpec extends Spec with InetSocketAddressReaders {
  def is = s2"""
  The InetSocketAddress value readers should
    read a valid named InetSocketAddress $readValidNamedInetSocketAddress
    read a valid raw InetSocketAddress $readValidRawInetSocketAddress
    detect wrong type on a malformed InetSocketAddress $readMalformedInetSocketAddress
    read a valid comma-separated list of InetSocketAddresses $readValidInetSocketAddresses
    read a valid comma-separated list of InetSocketAddresses surrounded by whitespace $readValidInetSocketAddressesWithWhiteSpace
    detect wrong type on malformed InetSocketAddresses $readMalformedInetSocketAddresses
    be able to read a single InetSocketAddress as a list of InetSocketAddresses $readSingleInetSocketAddress
  """

  def readValidNamedInetSocketAddress = {
    val inetSocketAddress = """localhost:65535"""
    val cfg = ConfigFactory.parseString(s"myValue = ${"\"" + inetSocketAddress + "\""}")
    inetSocketAddressReader.read(cfg, "myValue") must beEqualTo(new InetSocketAddress("localhost", 65535))
  }

  def readValidRawInetSocketAddress = {
    val inetSocketAddress = """127.0.0.1:65535"""
    val cfg = ConfigFactory.parseString(s"myValue = ${"\"" + inetSocketAddress + "\""}")
    inetSocketAddressReader.read(cfg, "myValue") must beEqualTo(new InetSocketAddress("127.0.0.1", 65535))
  }

  def readMalformedInetSocketAddress = {
    val malformedInetSocketAddress = """localhost123"""
    val cfg = ConfigFactory.parseString(s"myValue = ${"\"" + malformedInetSocketAddress + "\""}")
    inetSocketAddressReader.read(cfg, "myValue") must throwA[WrongType]
  }

  def readValidInetSocketAddresses = {
    val inetSocketAddresses = """localhost:65535,localhost:80,localhost:443"""
    val cfg = ConfigFactory.parseString(s"myValue = ${"\"" + inetSocketAddresses + "\""}")
    inetSocketAddressListReader.read(cfg, "myValue") must beEqualTo(
      List(
      new InetSocketAddress("localhost", 65535),
        new InetSocketAddress("localhost", 80),
        new InetSocketAddress("localhost", 443)
      )
    )
  }

  def readValidInetSocketAddressesWithWhiteSpace = {
    val inetSocketAddresses = """localhost: 65535, localhost: 80, localhost: 443"""
    val cfg = ConfigFactory.parseString(s"myValue = ${"\"" + inetSocketAddresses + "\""}")
    inetSocketAddressListReader.read(cfg, "myValue") must beEqualTo(
      List(
        new InetSocketAddress("localhost", 65535),
        new InetSocketAddress("localhost", 80),
        new InetSocketAddress("localhost", 443)
      )
    )
  }

  def readMalformedInetSocketAddresses = {
    val malformedInetSocketAddresses = """localhost:65535 + localhost:80"""
    val cfg = ConfigFactory.parseString(s"myValue = ${"\"" + malformedInetSocketAddresses + "\""}")
    inetSocketAddressListReader.read(cfg, "myValue") must throwA[WrongType]
  }

  def readSingleInetSocketAddress = {
    val inetSocketAddress = """localhost:65535"""
    val cfg = ConfigFactory.parseString(s"myValue = ${"\"" + inetSocketAddress + "\""}")
    inetSocketAddressListReader.read(cfg, "myValue") must beEqualTo(
      List(new InetSocketAddress("localhost", 65535))
    )
  }
}
