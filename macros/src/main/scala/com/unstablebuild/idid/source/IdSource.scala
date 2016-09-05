package com.unstablebuild.idid.source

trait IdSource[UID] {

  def random: UID

  def parse(str: String): UID

  def empty: UID

}
