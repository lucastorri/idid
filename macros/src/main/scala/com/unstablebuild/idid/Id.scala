package com.unstablebuild.idid

import com.unstablebuild.idid.factory.IdFactory
import scala.language.experimental.macros

trait Id {

  type UID

  def underlying: UID

  override final def equals(obj: scala.Any): Boolean = obj match {
    case that: Id if that.getClass == this.getClass => that.underlying == this.underlying
    case _ => false
  }

  override final def hashCode: Int = underlying.hashCode

  override final def toString: String = underlying.toString

}

object Id {

  def create[T <: Id : IdFactory](uid: T#UID): T =
    implicitly[IdFactory[T]].create(uid)

  def empty[T <: Id : IdFactory]: T =
    implicitly[IdFactory[T]].empty

  def parse[T <: Id : IdFactory](str: String): T =
    implicitly[IdFactory[T]].parse(str)

  def random[T <: Id : IdFactory]: T =
    implicitly[IdFactory[T]].random

  def value[T <: Id : IdFactory](id: T): T#UID =
    id.underlying

  def factory[T <: Id]: IdFactory[T] =
    macro Macros.factoryImpl[T]

}

trait TypedId[T] extends Id {

  override type UID = T

}
