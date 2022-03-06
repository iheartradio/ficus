package net.ceedubs.ficus

import org.specs2._

trait Scala3Compat extends Specification {
  implicit final class MustEqualExtension[A](a1: A) {
    def must_==(a2: A) = a1 must beEqualTo(a2)
  }
}
