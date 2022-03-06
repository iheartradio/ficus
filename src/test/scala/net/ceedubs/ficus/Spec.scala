package net.ceedubs.ficus

import org.specs2.matcher.MustMatchers
import org.specs2.{ScalaCheck, Specification}

trait Spec extends Specification with MustMatchers with ScalaCheck with Scala3Compat
