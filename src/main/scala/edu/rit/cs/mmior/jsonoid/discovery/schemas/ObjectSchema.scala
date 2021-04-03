package edu.rit.cs.mmior.jsonoid.discovery
package schemas

import org.json4s.JsonDSL._

object ObjectSchema {
  def apply(value: Map[String, JsonSchema[_]]): ObjectSchema = {
    ObjectSchema(
      SchemaProperties(
        ObjectTypesProperty()
      ).merge(value)
    )
  }
}

case class ObjectSchema(
    override val properties: SchemaProperties[Map[String, JsonSchema[_]]] =
      SchemaProperties.empty
) extends JsonSchema[Map[String, JsonSchema[_]]] {
  override val schemaType = "object"

  def mergeSameType: PartialFunction[JsonSchema[_], JsonSchema[_]] = {
    case other @ ObjectSchema(otherProperties) =>
      ObjectSchema(properties.merge(otherProperties))
  }
}

case class ObjectTypesProperty(
    objectTypes: Map[String, JsonSchema[_]] = Map.empty[String, JsonSchema[_]]
) extends SchemaProperty[Map[String, JsonSchema[_]]] {
  override val toJson = ("properties" -> "foo")

  override def merge(otherProp: SchemaProperty[Map[String, JsonSchema[_]]]) = {
    val other = otherProp.asInstanceOf[ObjectTypesProperty].objectTypes
    this.merge(other)
  }

  override def merge(value: Map[String, JsonSchema[_]]) = {
    val merged = objectTypes.toSeq ++ value.toSeq
    val grouped = merged.groupBy(_._1)
    ObjectTypesProperty(
      grouped.view.mapValues(_.map(_._2).reduce(_.merge(_))).toMap
    )
  }
}