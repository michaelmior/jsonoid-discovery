package edu.rit.cs.mmior.jsonoid.discovery
package schemas

import org.json4s._

final case class NullSchema(
    override val properties: SchemaProperties[Nothing] =
      SchemaProperties.empty[Nothing]
) extends JsonSchema[Nothing] {
  override val schemaType = "null"

  override val validTypes: Set[Class[_]] = Set.empty

  override def mergeSameType(mergeType: MergeType)(implicit
      er: EquivalenceRelation
  ): PartialFunction[JsonSchema[_], JsonSchema[_]] = {
    case other @ NullSchema(otherProperties) =>
      NullSchema(properties.merge(otherProperties, mergeType))
  }

  override def copy(properties: SchemaProperties[Nothing]): NullSchema =
    NullSchema(properties)

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  override def isValidType[S <: JValue](value: S): Boolean = value == JNull
}
