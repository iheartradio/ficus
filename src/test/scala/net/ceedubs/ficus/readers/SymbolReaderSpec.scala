package net.ceedubs.ficus
package readers

import com.typesafe.config.ConfigFactory
import ConfigSerializerOps._

class SymbolReaderSpec extends Spec with SymbolReader {
  def is = s2"""
  The Symbol value reader should
    read a Symbol $readSymbol
  """

  def readSymbol = prop { (string: String) =>
    val cfg = ConfigFactory.parseString(s"myValue = ${string.asConfigValue}")
    symbolValueReader.read(cfg, "myValue") must beEqualTo(Symbol(string))
  }
}
