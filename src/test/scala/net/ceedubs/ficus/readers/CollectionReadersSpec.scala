package net.ceedubs.ficus
package readers

import com.typesafe.config.ConfigFactory
import AnyValReaders.{booleanValueReader, doubleValueReader, intValueReader, longValueReader}
import StringReader.stringValueReader
import ConfigSerializerOps._
import org.scalacheck.util.Buildable
import org.scalacheck.Arbitrary
import CollectionReaderSpec._
import scala.language.higherKinds

class CollectionReadersSpec extends Spec with CollectionReaders {
  def is = s2"""
  The collection value readers should
    read a list ${readCollection[List]}
    read a set ${readCollection[Set]}
    read an array ${readCollection[Array]}
    read an indexed sequence ${readCollection[IndexedSeq]}
    read a vector ${readCollection[Vector]}
    read an iterable $readIterable
    read a map with strings as keys $readStringMap
    read a map nested in another object $readNestedMap
    read a collection when used directly $readCollectionUsedDirectly
  """

  def readIterable = {
    implicit def iterableSerializer[A: ConfigSerializer]: ConfigSerializer[Iterable[A]] =
      ConfigSerializer.iterableSerializer
    readCollection[Iterable]
  }

  def readStringMap = {
    def reads[A: Arbitrary: ValueReader: ConfigSerializer] = prop { (map: Map[String, A]) =>
      val cfg = ConfigFactory.parseString(s"myValue = ${map.asConfigValue}")
      mapValueReader[A].read(cfg, "myValue") must beEqualTo(map)
    }

    reads[String] && reads[Boolean] && reads[Int] && reads[Long] && reads[Double]
  }

  def readNestedMap = {
    val cfg = ConfigFactory.parseString("""
        |wrapper {
        |  myValue {
        |    item1 = "value1"
        |    item2 = "value2"
        |  }
        |}
      """.stripMargin)
    mapValueReader[String].read(cfg, "wrapper.myValue") must beEqualTo(Map("item1" -> "value1", "item2" -> "value2"))
  }

  protected def readCollection[C[_]](implicit
      AS: Arbitrary[C[String]],
      SS: ConfigSerializer[C[String]],
      RS: ValueReader[C[String]],
      AB: Arbitrary[C[Boolean]],
      SB: ConfigSerializer[C[Boolean]],
      RB: ValueReader[C[Boolean]],
      AI: Arbitrary[C[Int]],
      SI: ConfigSerializer[C[Int]],
      RI: ValueReader[C[Int]],
      AL: Arbitrary[C[Long]],
      SL: ConfigSerializer[C[Long]],
      RL: ValueReader[C[Long]],
      AD: Arbitrary[C[Double]],
      SD: ConfigSerializer[C[Double]],
      RD: ValueReader[C[Double]]
  ) = {

    def reads[V](implicit arb: Arbitrary[C[V]], serializer: ConfigSerializer[C[V]], reader: ValueReader[C[V]]) =
      prop { (values: C[V]) =>
        val cfg = ConfigFactory.parseString(s"myValue = ${values.asConfigValue}")
        reader.read(cfg, "myValue") must beEqualTo(values)
      }

    reads[String] && reads[Boolean] && reads[Int] && reads[Long] && reads[Double]
  }

  def readCollectionUsedDirectly = {
    val cfg = ConfigFactory.parseString("set: [1, 2, 2, 3]")
    traversableReader[Set, Int].read(cfg, "set") must beEqualTo(Set(1, 2, 3))
  }

}

object CollectionReaderSpec {
  import scala.collection._

  implicit def buildableIndexedSeq[T]: Buildable[T, IndexedSeq[T]] = new Buildable[T, IndexedSeq[T]] {
    def builder = IndexedSeq.newBuilder[T]
  }

  implicit def buildableVector[T]: Buildable[T, Vector[T]] = new Buildable[T, Vector[T]] {
    def builder = Vector.newBuilder[T]
  }

  implicit def buildableIterable[T]: Buildable[T, Iterable[T]] = new Buildable[T, Iterable[T]] {
    def builder = new mutable.ListBuffer[T]
  }
}
