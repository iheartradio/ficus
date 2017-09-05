package net.ceedubs.ficus.readers

import com.typesafe.config.Config

/**
  * A CompositeValueReader is a spiritual cousin of ValueReader.  Whereas a ValueReader wants to read a single value from a single
  * path in a Config object, a CompositeValueReader instead reads a single value using the entire Config passed in.
  * @tparam A
  */
trait CompositeValueReader[A]{ self =>

  /**
    * Convert the `config` object into an `A`
    * @param config
    * @return
    */
  def read(config : Config) : A

  /**
    * Turns a CompositeValueReader[A] into a CompositeValueReader[B] by applying the provided transformation `f` on the item of type A
    * that is read from config
    */
  def map[B](f: A => B): CompositeValueReader[B] = new CompositeValueReader[B] {
    def read(config: Config): B = f(self.read(config))
  }
}
