package net.ceedubs.ficus.readers

trait CaseInsensitiveEnumerationReader extends EnumerationReader {

  override protected def findEnumValue[T <: Enumeration](enum: T, configValue: String): Option[T#Value] =
    enum.values.find(_.toString.toLowerCase == configValue.toLowerCase)
}
