package com.unstablebuild.idid

import java.util.UUID

import com.unstablebuild.idid.factory.IdFactory
import org.scalatest.{FlatSpec, MustMatchers}

class IdentityTest extends FlatSpec with MustMatchers {

  case class MyOwnId(underlying: UUID) extends TypedId[UUID]
  implicit val myOwnIdFactory = Id.factory[MyOwnId]

  it must "allow creating ids" in {
    val uuid = UUID.randomUUID()

    val id = Id.create[MyOwnId](uuid)
    Id.value(id) must equal (uuid)
  }

  it must "return the underlying id as the string representation" in {
    val uuid = UUID.randomUUID()

    Id.create[MyOwnId](uuid).toString must equal (uuid.toString)
  }

  it must "implements equals and hashCode" in {
    val id1 = Id.random[MyOwnId]
    val id2 = Id.parse[MyOwnId](id1.toString)

    id1 must equal (id2)
    id1.hashCode must equal (id2.hashCode)
  }

  it must "allow the original class to be used normally" in {
    val id1 = Id.random[MyOwnId]
    val id2 = MyOwnId(id1.underlying)

    id1 must equal (id2)
    id1.hashCode must equal (id2.hashCode)
  }

  it must "support multiple types with the same underlying id" in {
    case class MyOtherId(underlying: UUID) extends TypedId[UUID]
    implicit val myOtherIdFactory = Id.factory[MyOtherId]

    val c1 = Id.random[MyOwnId]
    val c2 = Id.create[MyOtherId](Id.value[MyOwnId](c1))

    c1 must not equal c2
    c1.hashCode must equal (c2.hashCode)
  }

  it must "implement a base interface" in {
    case class MyOtherId(underlying: String) extends TypedId[String]
    implicit val myOtherIdFactory = Id.factory[MyOtherId]

    val ids: Set[Id] = Set(Id.random[MyOwnId], Id.random[MyOtherId])

    ids.size must equal (2)
  }

  it must "allow custom functions to use the ids" in {

    val id = Id.random[MyOwnId]
    def parse[T <: Id : IdFactory](str: String): T = Id.parse[T](str)

    parse[MyOwnId](id.toString) must equal (id)
  }

}
