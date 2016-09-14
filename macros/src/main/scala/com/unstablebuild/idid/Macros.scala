package com.unstablebuild.idid

import com.unstablebuild.idid.factory.IdFactory
import com.unstablebuild.idid.source.IdSource

import scala.language.experimental.macros
import scala.reflect.macros.blackbox

object Macros {

  def factoryImpl[T <: Id : c.WeakTypeTag](c: blackbox.Context): c.Expr[IdFactory[T]] = {
    import c.universe._

    val typ = weakTypeOf[T]
    val sourceType = weakTypeOf[IdSource[Any]]
    val factoryType = weakTypeOf[IdFactory[T]]

    val (uidName, uidType) = {
      val uidParam = typ.decl(termNames.CONSTRUCTOR).asMethod.paramLists.flatten.head.asTerm
      (uidParam.name, uidParam.typeSignature)
    }

    c.Expr[IdFactory[T]](q"""new ${factoryType.typeSymbol}[$typ] {

      private val source = implicitly[${sourceType.typeSymbol}[$uidType]]

      override def create(uid: $uidType): $typ = new $typ(uid)

      override def empty: $typ = create(source.empty)

      override def parse(str: String): $typ = create(source.parse(str))

      override def random: $typ = create(source.random)

      override def value(id: $typ): $uidType = id.$uidName

    }""")
  }

}
