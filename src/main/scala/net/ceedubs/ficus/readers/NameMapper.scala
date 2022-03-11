package net.ceedubs.ficus.readers

/** Defines an object that knows to map between names as they found in the code to those who should be defined in the
 *  configuration
 */
trait NameMapper {

  /** Maps between the name in the code to name in configuration
   *  @param name
   *    The name as found in the code
   */
  def map(name: String): String

}

/** Helper object to get the current name mapper
 */
object NameMapper {

  /** Gets the name mapper from the implicit scope
   *  @param nameMapper
   *    The name mapper from the implicit scope, or the default name mapper if not found
   *  @return
   *    The name mapper to be used in current implicit scope
   */
  def apply()(implicit nameMapper: NameMapper = DefaultNameMapper): NameMapper = nameMapper

}

/** Default implementation for name mapper, names in code equivalent to names in configuration
 */
case object DefaultNameMapper extends NameMapper {

  override def map(name: String): String = name

}
