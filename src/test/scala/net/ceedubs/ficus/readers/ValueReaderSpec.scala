package net.ceedubs.ficus
package readers

import com.typesafe.config.ConfigFactory
import Ficus.intValueReader

class ValueReaderSpec extends Spec {
  def is = s2"""
  A value reader should
    be able to be fetched from implicit scope via the companion apply method $fromCompanionApply
    be a functor $transformAsFunctor
  """

  def fromCompanionApply =
    ValueReader[Int] must beEqualTo(implicitly[ValueReader[Int]])

  def transformAsFunctor = {
    val plusOneReader = ValueReader[Int].map(_ + 1)
    prop { (i: Int) =>
      val cfg = ConfigFactory.parseString(s"myValue = $i")
      plusOneReader.read(cfg, "myValue") must beEqualTo(i + 1)
    }
  }

}
