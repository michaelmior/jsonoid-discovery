package edu.rit.cs.mmior.jsonoid.discovery.schemas

object SchemaProperties {
  type PropertySubclass[T] = Class[_ <: SchemaProperty[T]]
  type PropertyMap[T] = Map[PropertySubclass[T], SchemaProperty[T]]

  def apply[T](properties: SchemaProperty[T]*): SchemaProperties[T] = {
    SchemaProperties(
      properties
        .map(prop => (prop.getClass, prop))
        .asInstanceOf[Seq[(PropertySubclass[T], SchemaProperty[T])]]
        .toMap
    )
  }

  def apply[T](properties: Iterable[SchemaProperty[T]]): SchemaProperties[T] = {
    SchemaProperties(
      properties
        .map(prop => (prop.getClass, prop))
        .asInstanceOf[Seq[(PropertySubclass[T], SchemaProperty[T])]]
        .toMap
    )
  }

  def empty[T]: SchemaProperties[T] = {
    SchemaProperties[T]()
  }
}

import SchemaProperties._

final case class SchemaProperties[T](
    val properties: PropertyMap[T] =
      Map.empty[PropertySubclass[T], SchemaProperty[T]]
) extends Iterable[SchemaProperty[T]] {

  override def iterator: Iterator[SchemaProperty[T]] =
    properties.values.iterator

  def merge(value: T): SchemaProperties[T] = {
    val mergedProperties = properties.map { case (key, prop) =>
      prop.merge(value)
    }
    SchemaProperties(mergedProperties)
  }

  def merge(other: SchemaProperties[T]): SchemaProperties[T] = {
    val mergedProperties = properties.map { case (key, prop) =>
      other.properties.get(key) match {
        case Some(otherProp) => prop.merge(otherProp)
        case None            => prop
      }
    }
    SchemaProperties(mergedProperties)
  }
}
