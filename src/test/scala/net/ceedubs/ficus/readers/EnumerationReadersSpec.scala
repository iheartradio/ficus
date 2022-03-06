package net.ceedubs.ficus.readers

import com.typesafe.config.{ConfigException, ConfigFactory}
import net.ceedubs.ficus.Spec
import EnumerationReadersSpec._

import scala.reflect.ClassTag

class EnumerationReadersSpec extends Spec with EnumerationReader {
  def is = s2"""
  An enumeration value reader should
    map a string value to its enumeration counterpart $successStringMapping
    map a int value to its enumeration counterpart $successIntMapping
    throw exception if value couldn't be converted to enum value $invalidMapping
    throw exception if enumeration is contained in a class or trait $notInstantiable
    throw exception if enumeration is not an object $notObject
  """

  def successStringMapping = {
    val cfg               = ConfigFactory.parseString("myValue = SECOND")
    implicit val classTag = ClassTag[StringValueEnum.type](StringValueEnum.getClass)
    enumerationValueReader[StringValueEnum.type].read(cfg, "myValue") must_== StringValueEnum.second
  }

  def successIntMapping = {
    val cfg               = ConfigFactory.parseString("myValue = second")
    implicit val classTag = ClassTag[IntValueEnum.type](IntValueEnum.getClass)
    enumerationValueReader[IntValueEnum.type].read(cfg, "myValue") must_== IntValueEnum.second
  }

  def invalidMapping = {
    val cfg               = ConfigFactory.parseString("myValue = fourth")
    implicit val classTag = ClassTag[StringValueEnum.type](StringValueEnum.getClass)
    enumerationValueReader[StringValueEnum.type].read(cfg, "myValue") must throwA[ConfigException.BadValue]
  }

  def notInstantiable = {
    val cfg               = ConfigFactory.parseString("myValue = fourth")
    implicit val classTag = ClassTag[InnerEnum.type](InnerEnum.getClass)
    enumerationValueReader[InnerEnum.type].read(cfg, "myValue") must throwA[ConfigException.Generic]
  }

  def notObject = {
    val cfg               = ConfigFactory.parseString("myValue = fourth")
    implicit val classTag = ClassTag[NotObject](classOf[NotObject])
    enumerationValueReader[NotObject].read(cfg, "myValue") must throwA[ConfigException.Generic]
  }

  object InnerEnum extends Enumeration
}

object EnumerationReadersSpec {

  object StringValueEnum extends Enumeration {
    val first  = Value("FIRST")
    val second = Value("SECOND")
    val third  = Value("THIRD")
  }

  object IntValueEnum extends Enumeration {
    val first, second, third = Value
  }

  class NotObject extends Enumeration
}
