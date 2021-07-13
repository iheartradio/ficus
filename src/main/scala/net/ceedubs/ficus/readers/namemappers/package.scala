package net.ceedubs.ficus.readers

package object namemappers {
  object implicits {
    implicit val hyphenCase: NameMapper = HyphenNameMapper
  }
}
