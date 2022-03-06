package net.ceedubs.ficus.readers

import com.typesafe.config.ConfigException.{BadValue, Generic}
import com.typesafe.config.Config
import net.ceedubs.ficus.util.EnumerationUtil.EnumValue
import scala.reflect.ClassTag
import scala.util.{Failure, Success, Try}

trait EnumerationReader {
  implicit def enumerationValueReader[T <: Enumeration: ClassTag]: ValueReader[EnumValue[T]] =
    new ValueReader[EnumValue[T]] {
      def read(config: Config, path: String): EnumValue[T] = {
        val c      = implicitly[ClassTag[T]].runtimeClass
        val `enum` = Try(c.getField("MODULE$")) match {
          case Success(m) => m.get(null).asInstanceOf[T]
          case Failure(e) =>
            throw new Generic(
              "Cannot get instance of enum: " + c.getCanonicalName + "; " +
                "make sure the enum is an object and it's not contained in a class or trait",
              e
            )
        }

        val value = config.getString(path)
        findEnumValue(`enum`, value)
          .getOrElse(
            throw new BadValue(
              config.origin(),
              path,
              value + " isn't a valid value for enum: " +
                "" + c.getCanonicalName + "; allowed values: " + `enum`.values.mkString(", ")
            )
          )
          .asInstanceOf[EnumValue[T]]
      }
    }

  protected def findEnumValue[T <: Enumeration](`enum`: T, configValue: String): Option[EnumValue[T]] =
    `enum`.values.find(_.toString == configValue)
}

object EnumerationReader extends EnumerationReader
