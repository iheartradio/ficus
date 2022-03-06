package net.ceedubs.ficus

import com.typesafe.config.Config
import net.ceedubs.ficus.readers._
import scala.language.implicitConversions

trait FicusInstances
    extends AnyValReaders
    with StringReader
    with SymbolReader
    with OptionReader
    with CollectionReaders
    with ConfigReader
    with DurationReaders
    with TryReader
    with ConfigValueReader
    with BigNumberReaders
    with ISOZonedDateTimeReader
    with PeriodReader
    with LocalDateReader
    with ChronoUnitReader
    with URIReaders
    with URLReader
    with InetSocketAddressReaders

object Ficus extends FicusInstances {
  implicit def toFicusConfig(config: Config): FicusConfig = SimpleFicusConfig(config)
}
