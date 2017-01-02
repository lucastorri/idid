package com.unstablebuild.idid.factory

import com.unstablebuild.idid.{Id, Macros}

import scala.language.experimental.macros

trait AutoIdFactory {

  implicit def factory[T <: Id]: IdFactory[T] = macro Macros.factoryImpl[T]

}
