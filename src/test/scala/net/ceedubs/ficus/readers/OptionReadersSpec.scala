package net.ceedubs.ficus
package readers

import com.typesafe.config.ConfigFactory

class OptionReadersSpec extends Spec with OptionReader with AnyValReaders {
  def is = s2"""
  An option value reader should
    wrap an existing value in a Some $optionSome
    return a None for a non-existing value $optionNone
  """

  def optionSome = prop { (i: Int) =>
    val cfg = ConfigFactory.parseString(s"myValue = $i")
    optionValueReader[Int].read(cfg, "myValue") must beSome(i)
  }

  def optionNone = {
    val cfg = ConfigFactory.parseString("")
    optionValueReader[Boolean].read(cfg, "myValue") must beNone
  }
}
