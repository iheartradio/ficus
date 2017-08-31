package net.ceedubs.ficus.readers

import com.typesafe.config.{ConfigException, Config}

trait BigNumberReaders {
  implicit val bigDecimalReader: ValueReader[BigDecimal] = new ValueReader[BigDecimal] {
    def read(config: Config, path: String): BigDecimal = {
      val s = config.getString(path)
      try {
        BigDecimal(s)
      } catch {
        case e: NumberFormatException => throw new ConfigException.WrongType(config.origin(),path,"scala.math.BigDecimal","String",e)
      }
    }
  }

  implicit val bigIntReader: ValueReader[BigInt] = new ValueReader[BigInt] {
    def read(config: Config, path: String): BigInt = {
      val s = config.getString(path)
      try {
        BigInt(s)
      } catch {
        case e: NumberFormatException => throw new ConfigException.WrongType(config.origin(),path,"scala.math.BigInt","String",e)
      }
    }
  }
}

object BigNumberReaders extends BigNumberReaders
