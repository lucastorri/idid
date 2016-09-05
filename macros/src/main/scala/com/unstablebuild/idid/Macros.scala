package com.unstablebuild.idid

import com.unstablebuild.idid.factory.IdFactory
import com.unstablebuild.idid.source.IdSource

import scala.language.experimental.macros
import scala.reflect.macros.blackbox

object Macros {

  def identityImpl(c: blackbox.Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._

    val baseType = typeOf[Id]
    val sourceType = typeOf[IdSource[Any]]
    val factoryType = typeOf[IdFactory[Id]]

    // the annotation is the token before our class
    val q"new $_[${idType: Ident}]" = c.prefix.tree

    val clzName = annottees.head.tree match {
      case q"""..$modifiers class ${name: Name}(..$params) extends ..$parents { ..$body }""" =>
        name
      case _ =>
        c.abort(c.enclosingPosition, "Failed to match class")
    }

    // the companion object comes together with the annottess
    val (objModifiers, objParents, objBody) = annottees.collectFirst {
      case q"""..${modifiers: Modifiers} object $name extends ..$parents { ..${body: Seq[Tree]} }""" =>
        (modifiers, parents.asInstanceOf[Seq[RefTree]], body)
    }.getOrElse((Modifiers(), Seq.empty, Seq.empty))

    c.Expr[Any](q"""

      final case class ${clzName.toTypeName}(underlying: $idType) extends $baseType {

        override type UID = $idType

        override def toString: String = underlying.toString

      }

      $objModifiers object ${clzName.toTermName} extends ..$objParents {

        ..$objBody

        val source = implicitly[${sourceType.typeSymbol}[$idType]]

        implicit val factory: ${factoryType.typeSymbol}[${clzName.toTypeName}] = new ${factoryType.typeSymbol}[${clzName.toTypeName}] {

          override def create(uid: $idType): ${clzName.toTypeName} = new ${clzName.toTypeName}(uid)

          override def empty: ${clzName.toTypeName} = create(source.empty)

          override def parse(str: String): ${clzName.toTypeName} = create(source.parse(str))

          override def random: ${clzName.toTypeName} = create(source.random)

          override def value(id: ${clzName.toTypeName}): $idType = id.underlying

        }

      }

    """)
  }

}
