package net.ceedubs.ficus.util

private[ficus] object EnumerationUtil {
  private[this] type Aux[A] = { type Value = A }

  type EnumValue[A <: Enumeration] = A match {
    case Aux[a] => a
  }
}
