package net.ceedubs.ficus.readers.namemappers

import net.ceedubs.ficus.readers.NameMapper

object HyphenNameMapperNoDigits extends NameMapper {
  private lazy val r = "((?<=[a-z0-9])[A-Z]|(?!^)[A-Z](?=[a-z]))".r

  /** Maps from a camelCasedName to a hyphenated-name
   */
  override def map(name: String): String = r.replaceAllIn(name, m => s"-${m.group(1)}").toLowerCase
}
