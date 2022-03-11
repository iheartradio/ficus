package net.ceedubs.ficus.readers

import com.typesafe.config.Config

/** Reads a value of type A that is located at a provided `path` in a Config. */
trait ValueReader[A] { self =>

  /** Reads the value at the path `path` in the Config */
  def read(config: Config, path: String): A

  /** Turns a ValueReader[A] into a ValueReader[B] by applying the provided transformation `f` on the item of type A
   *  that is read from config
   */
  def map[B](f: A => B): ValueReader[B] = new ValueReader[B] {
    def read(config: Config, path: String): B = f(self.read(config, path))
  }
}

object ValueReader {
  implicit def generatedReader[A](implicit generated: Generated[ValueReader[A]]): ValueReader[A] = generated.value

  /** Returns the implicit ValueReader[A] in scope. `ValueReader[A]` is equivalent to `implicitly[ValueReader[A]]`
   */
  def apply[A](implicit reader: ValueReader[A]): ValueReader[A] = reader

  /** ValueReader that receives a Config whose root is the path being read.
   *
   *  This is generally the most concise way to implement a ValueReader that doesn't depend on the path of the value
   *  being read.
   *
   *  For example to read a `case class FooBar(foo: Foo, bar: Bar)`, instead of
   *  {{{
   *   new ValueReader[FooBar] {
   *     def read(config: Config, path: String): FooBar = {
   *       val localizedConfig = config.getConfig(path)
   *       FooBar(
   *         foo = localizedConfig.as[Foo]("foo"),
   *         bar = localizedConfig.as[Bar]("bar"))
   *     }
   *   }
   *  }}}
   *  you could do
   *  {{{
   *  ValueReader.relative[FooBar] { config =>
   *    FooBar(
   *      foo = config.as[Foo]("foo"),
   *      bar = config.as[Bar]("bar))
   *  }
   *  }}}
   */
  def relative[A](f: Config => A): ValueReader[A] = new ValueReader[A] {
    def read(config: Config, path: String): A = f(config.getConfig(path))
  }
}
