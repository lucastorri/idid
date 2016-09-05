package com.unstablebuild.idid

import java.util.UUID

import com.unstablebuild.idid.factory.IdFactory
import org.scalatest.{FlatSpec, MustMatchers}
import com.unstablebuild.idid.source._

/**
  * TODO
  * case class DeviceId(uid: UUID) extends AnyVal with Id[UUID]
  * implicit val deviceIdFactory = Id.factory[DeviceId]
  */
class IdentityTest extends FlatSpec with MustMatchers {

  it must "allow creating ids" in {
    val uuid = UUID.randomUUID()

    val id = Id.create[CustomUUIDId](uuid)
    Id.value(id) must equal (uuid)
  }

  it must "return the underlying id as the string representation" in {
    val uuid = UUID.randomUUID()

    Id.create[CustomUUIDId](uuid).toString must equal (uuid.toString)
  }

  it must "implements equals and hashCode" in {
    val id1 = Id.random[CustomUUIDId]
    val id2 = Id.parse[CustomUUIDId](id1.toString)

    id1 must equal (id2)
    id1.hashCode must equal (id2.hashCode)
  }

  it must "support multiple types with the same underlying id" in {
    @Identity[UUID]
    class Custom2

    val c1 = Id.random[CustomUUIDId]
    val c2 = Id.create[Custom2](Id.value[CustomUUIDId](c1))

    c1 must not equal c2
    c1.hashCode must equal (c2.hashCode)
  }

  it must "implement a base interface" in {
    @Identity[UUID]
    class Custom2

    val ids: Set[Id] = Set(Id.random[CustomUUIDId], Id.random[Custom2])

    ids.size must equal (2)
  }

  it must "allow custom functions to use the ids" in {

    val id = Id.random[CustomUUIDId]
    def parse[T <: Id : IdFactory](str: String): T = Id.parse(str)

    parse[CustomUUIDId](id.toString) must equal (id)
  }

}


@Identity[UUID]
class CustomUUIDId
