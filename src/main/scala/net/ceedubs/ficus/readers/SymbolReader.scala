package net.ceedubs.ficus.readers

import com.typesafe.config.Config

trait SymbolReader {
  implicit val symbolValueReader: ValueReader[Symbol] = new ValueReader[Symbol] {
    def read(config: Config, path: String): Symbol = Symbol(config.getString(path))
  }
}

object SymbolReader extends SymbolReader
