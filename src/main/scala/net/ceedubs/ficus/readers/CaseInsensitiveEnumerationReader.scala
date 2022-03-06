package net.ceedubs.ficus.readers

import net.ceedubs.ficus.util.EnumerationUtil.EnumValue

trait CaseInsensitiveEnumerationReader extends EnumerationReader {

  override protected def findEnumValue[T <: Enumeration](`enum`: T, configValue: String): Option[EnumValue[T]] =
    `enum`.values.find(_.toString.toLowerCase == configValue.toLowerCase)
}
