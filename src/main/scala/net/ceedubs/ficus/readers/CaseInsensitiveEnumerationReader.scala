package net.ceedubs.ficus.readers

import net.ceedubs.ficus.util.EnumerationUtil.EnumValue

trait CaseInsensitiveEnumerationReader extends EnumerationReader {

  override protected def findEnumValue[T <: Enumeration](`enum`: T, configValue: String): Option[`enum`.Value] =
    `enum`.values.find(_.toString.toLowerCase == configValue.toLowerCase)
}
