package net.ceedubs.ficus.readers

import com.typesafe.config.ConfigFactory
import net.ceedubs.ficus.{ConfigSerializer, Spec}
import net.ceedubs.ficus.ConfigSerializerOps._
import org.scalacheck.Arbitrary

import scala.util.{Failure, Try}
import scala.collection.JavaConverters._

class EitherReadersSpec
    extends Spec
    with EitherReader
    with OptionReader
    with AnyValReaders
    with StringReader
    with TryReader
    with CollectionReaders {
  def is = s2"""
    An Either value reader should
    should read right side when possible $readRightSideString
    fallback to left side when key is missing $fallbackToLeftSideOnMissingKey
    fallback to left when failing to read right $fallbackToLeftSideOnBadRightValue
    fail when both sides fail $rightAndLeftFailure
    handle a Try on the right side $rightSideTry
    handle a Try on the left side $leftSideTry
    handle complex types $handleComplexTypes
    """

  def readRightSideString = prop { (a: String) =>
    val cfg = a.toConfigValue.atKey("x")
    eitherReader[String, String].read(cfg, "x") must beEqualTo(Right(a))
  }

  def fallbackToLeftSideOnMissingKey = prop { (a: String) =>
    eitherReader[Option[String], String].read(ConfigFactory.empty(), "x") must beEqualTo(Left(None))
  }

  def fallbackToLeftSideOnBadRightValue = prop { (a: Int) =>
    val badVal = a.toString + "xx"
    eitherReader[String, Int].read(badVal.toConfigValue.atKey("x"), "x") must beEqualTo(Left(badVal))
  }

  def rightAndLeftFailure = prop { (a: Int) =>
    val badVal = a.toString + "xx"
    tryValueReader(eitherReader[Int, Int]).read(badVal.toConfigValue.atKey("x"), "x") must beAnInstanceOf[Failure[Int]]
  }

  def rightSideTry = prop { (a: Int) =>
    val badVal = a.toString + "xx"
    eitherReader[Int, Try[Int]].read(a.toConfigValue.atKey("x"), "x") must beRight(a)
    eitherReader[Int, Try[Int]].read(badVal.toConfigValue.atKey("x"), "x") must beRight(beFailedTry[Int])
  }

  def leftSideTry = prop { (a: Int) =>
    val badVal = a.toString + "xx"
    eitherReader[Try[String], Int].read(badVal.toConfigValue.atKey("x"), "x") must beLeft(
      beSuccessfulTry[String](badVal)
    )
    eitherReader[Try[Int], Int].read(badVal.toConfigValue.atKey("x"), "x") must beLeft(beFailedTry[Int])
  }

  def handleComplexTypes = prop { (a: Int, b: Int) =>
    val iMap = Map("a" -> a, "b" -> b)
    val sMap = Map("a" -> s"${a}xx", "b" -> s"${b}xx")

    eitherReader[Map[String, String], Map[String, String]].read(sMap.toConfigValue.atKey("a"), "a") must beRight(sMap)
    eitherReader[Map[String, String], Map[String, Int]].read(iMap.toConfigValue.atKey("a"), "a") must beRight(iMap)
    eitherReader[Map[String, String], Map[String, Int]].read(sMap.toConfigValue.atKey("a"), "a") must beLeft(sMap)
  }
}
