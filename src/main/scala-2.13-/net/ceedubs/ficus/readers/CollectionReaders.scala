package net.ceedubs.ficus.readers

import com.typesafe.config.{ConfigUtil, Config}
import collection.JavaConverters._
import collection.generic.CanBuildFrom
import scala.language.postfixOps
import scala.language.higherKinds

trait CollectionReaders {

  private[this] val DummyPathValue: String = "collection-entry-path"

  implicit def traversableReader[C[_], A](implicit entryReader: ValueReader[A], cbf: CanBuildFrom[Nothing, A, C[A]]): ValueReader[C[A]] = new ValueReader[C[A]] {
    def read(config: Config, path: String): C[A] = {
      val list = config.getList(path).asScala
      val builder = cbf()
      builder.sizeHint(list.size)
      list foreach { entry =>
        val entryConfig = entry.atPath(DummyPathValue)
        builder += entryReader.read(entryConfig, DummyPathValue)
      }
      builder.result
    }
  }

  implicit def mapValueReader[A](implicit entryReader: ValueReader[A]): ValueReader[Map[String, A]] = new ValueReader[Map[String, A]] {
    def read(config: Config, path: String): Map[String, A] = {
      val relativeConfig = config.getConfig(path)
      relativeConfig.root().entrySet().asScala map { entry =>
        val key = entry.getKey
        key -> entryReader.read(relativeConfig, ConfigUtil.quoteString(key))
      } toMap
    }
  }

}

object CollectionReaders extends CollectionReaders
