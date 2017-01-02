package com.unstablebuild.idid

import com.unstablebuild.idid.factory.{AutoIdFactory, IdFactory}

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

object Id extends IdFunctions {

  def factory[T <: Id]: IdFactory[T] =
    macro Macros.factoryImpl[T]

}

trait TypedId[T] extends Id {

  override type UID = T

}
