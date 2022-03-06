package net.ceedubs.ficus.readers

import net.ceedubs.ficus.Spec
import net.ceedubs.ficus.readers.namemappers.HyphenNameMapper
import org.scalacheck.Arbitrary
import org.scalacheck.Gen._
import org.specs2.matcher.DataTables

class HyphenNameMapperSpec extends Spec with DataTables {
  def is = s2"""
  A HyphenNameMapper should
    hyphenate a camelCased name $hyphenateCorrectly
    hyphenate a camelCased name containing digits $hyphenateWithDigits
  """

  def nonemptyStringListGen = nonEmptyListOf(alphaStr.suchThat(_.length > 1).map(_.toLowerCase))

  implicit def nonemptyStringList: Arbitrary[List[String]] = Arbitrary(nonemptyStringListGen)

  def hyphenateCorrectly = prop { (foos: List[String]) =>
    val camelCased = (foos.head +: foos.tail.map(_.capitalize)).mkString
    val hyphenated = foos.mkString("-").toLowerCase

    HyphenNameMapper.map(camelCased) must_== hyphenated
  }

  def hyphenateWithDigits =
    "camelCased" || "hyphenated" |>
      "camelCasedName67" !! "camel-cased-name-67" |
      "1144StartsWithA32422" !! "1144-starts-with-a-32422" |
      "get13HTML42Snippets" !! "get-13-html-42-snippets" |
      "thisOneIs13InThe43Middle" !! "this-one-is-13-in-the-43-middle" | { (camelCased, hyphenated) =>
        HyphenNameMapper.map(camelCased) must_== hyphenated
      }
}
