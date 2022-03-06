package net.ceedubs.ficus.util

private[ficus] object EnumerationUtil {
  type EnumValue[A <: Enumeration] = A#Value
}
