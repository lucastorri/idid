package com.unstablebuild.idid

import com.unstablebuild.idid.factory.IdFactory

trait Id {

  type UID

  def underlying: UID

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
    implicitly[IdFactory[T]].value(id)

}
