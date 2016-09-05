package com.unstablebuild.idid.factory

import com.unstablebuild.idid.Id

trait IdFactory[ID <: Id] {

  def create(uid: ID#UID): ID

  def empty: ID

  def parse(str: String): ID

  def random: ID

  def value(id: ID): ID#UID

}
