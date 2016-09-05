package com.unstablebuild.idid

import scala.annotation.{StaticAnnotation, compileTimeOnly}
import scala.language.experimental.macros

@compileTimeOnly("enable macro paradise to expand macro annotations")
class Identity[UID] extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro Macros.identityImpl
}
