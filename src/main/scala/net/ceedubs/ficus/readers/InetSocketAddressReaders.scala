package net.ceedubs.ficus.readers

import java.net.InetSocketAddress

import com.typesafe.config.{Config, ConfigException}

trait InetSocketAddressReaders {
  private def parseHostAndPort(unparsedHostAndPort: String): Option[InetSocketAddress] = {
    val hostAndPort = """([a-zA-Z0-9\.\-]+)\s*:\s*(\d+)""".r
    unparsedHostAndPort match {
      case hostAndPort(host, port) =>
        Some(new InetSocketAddress(host, port.toInt))
      case _                       =>
        None
    }
  }

  implicit val inetSocketAddressListReader: ValueReader[List[InetSocketAddress]] =
    new ValueReader[List[InetSocketAddress]] {
      def read(config: Config, path: String): List[InetSocketAddress] =
        try
          config
            .getString(path)
            .split(", *")
            .toList
            .map(parseHostAndPort)
            .partition(_.isEmpty) match {
            case (errors, ok) if errors.isEmpty =>
              ok.flatten
            case _                              =>
              throw new IllegalArgumentException("Cannot parse string into hosts and ports")
          }
        catch {
          case e: Exception =>
            throw new ConfigException.WrongType(config.origin(), path, "java.net.InetSocketAddress", "String", e)
        }
    }

  implicit val inetSocketAddressReader: ValueReader[InetSocketAddress] = new ValueReader[InetSocketAddress] {
    def read(config: Config, path: String): InetSocketAddress =
      try
        parseHostAndPort(config.getString(path))
          .getOrElse(throw new IllegalArgumentException("Cannot parse string into host and port"))
      catch {
        case e: Exception =>
          throw new ConfigException.WrongType(config.origin(), path, "java.net.InetSocketAddress", "String", e)
      }
  }
}

object InetSocketAddressReaders extends InetSocketAddressReaders
