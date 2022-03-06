package net.ceedubs.ficus.readers

import com.typesafe.config.ConfigFactory
import net.ceedubs.ficus.readers.EnumerationReadersSpec._

import scala.reflect.ClassTag

class CaseInsensitiveEnumerationReadersSpec extends EnumerationReadersSpec with CaseInsensitiveEnumerationReader {
  override def is = super.is.append(s2"""
      A case insensitive enumeration value reader should
        map a string value with different case to its enumeration counterpart $successMixedCaseMapping
      """)

  def successMixedCaseMapping = {
    val cfg               = ConfigFactory.parseString("myValue = secOND")
    implicit val classTag = ClassTag[StringValueEnum.type](StringValueEnum.getClass)
    enumerationValueReader[StringValueEnum.type].read(cfg, "myValue") must be equalTo StringValueEnum.second
  }
}
