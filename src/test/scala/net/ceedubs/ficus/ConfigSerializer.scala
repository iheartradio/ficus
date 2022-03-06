package net.ceedubs.ficus

import com.typesafe.config.{ConfigFactory, ConfigUtil, ConfigValue}

import scala.language.implicitConversions

trait ConfigSerializer[A] {
  def serialize(a: A): String
}

object ConfigSerializer {
  def apply[A](f: A => String): ConfigSerializer[A] = new ConfigSerializer[A] {
    def serialize(a: A): String = f(a)
  }

  def fromToString[A]: ConfigSerializer[A] = apply[A](_.toString)

  protected def serializeIterable[A](iterable: Iterable[A])(implicit serializer: ConfigSerializer[A]): String = {
    val elements = iterable.map(a => serializer.serialize(a))
    s"[${elements.mkString(", ")}]"
  }

  implicit val stringSerializer: ConfigSerializer[String]   = apply[String](ConfigUtil.quoteString)
  implicit val booleanSerializer: ConfigSerializer[Boolean] = fromToString[Boolean]
  implicit val intSerializer: ConfigSerializer[Int]         = fromToString[Int]
  implicit val longSerializer: ConfigSerializer[Long]       = fromToString[Long]
  implicit val doubleSerializer: ConfigSerializer[Double]   = fromToString[Double]

  implicit def listSerializer[A: ConfigSerializer]: ConfigSerializer[List[A]]             = apply[List[A]](serializeIterable)
  implicit def serializerForSets[A: ConfigSerializer]: ConfigSerializer[Set[A]]           = apply[Set[A]](serializeIterable)
  implicit def indexedSeqSerializer[A: ConfigSerializer]: ConfigSerializer[IndexedSeq[A]] =
    apply[IndexedSeq[A]](serializeIterable)
  implicit def vectorSerializer[A: ConfigSerializer]: ConfigSerializer[Vector[A]]         = apply[Vector[A]](serializeIterable)
  implicit def arraySerializer[A: ConfigSerializer]: ConfigSerializer[Array[A]]           = apply[Array[A]] { array =>
    serializeIterable(array.toIterable)
  }
  def iterableSerializer[A: ConfigSerializer]: ConfigSerializer[Iterable[A]]              = apply[Iterable[A]](serializeIterable)

  implicit def stringKeyMapSerializer[A](implicit
      valueSerializer: ConfigSerializer[A]
  ): ConfigSerializer[Map[String, A]] =
    new ConfigSerializer[Map[String, A]] {
      def serialize(map: Map[String, A]): String = {
        val lines = map.toIterable.map(
          Function.tupled((key, value) => s"${stringSerializer.serialize(key)} = ${valueSerializer.serialize(value)}")
        )
        s"{\n  ${lines.mkString("\n  ")}\n}"
      }
    }

}

final case class ConfigSerializerOps[A](a: A, serializer: ConfigSerializer[A]) {
  def asConfigValue: String      = serializer.serialize(a)
  def toConfigValue: ConfigValue = ConfigFactory.parseString(s"dummy=$asConfigValue").root().get("dummy")
}

object ConfigSerializerOps {
  implicit def toConfigSerializerOps[A](a: A)(implicit serializer: ConfigSerializer[A]): ConfigSerializerOps[A] =
    ConfigSerializerOps[A](a, serializer)
}
