package com.unstablebuild.idid

import java.util.UUID

import scala.util.Random

package object source {

  implicit val uuidSource: IdSource[UUID] = new IdSource[UUID] {
    override def random: UUID = UUID.randomUUID()
    override def parse(str: String): UUID = UUID.fromString(str)
    override def empty: UUID = UUID.fromString("00000000-0000-0000-0000-000000000000")
  }

  implicit val intSource: IdSource[Int] = new IdSource[Int] {
    override def random: Int = Random.nextInt()
    override def parse(str: String): Int = str.toInt
    override def empty: Int = 0
  }

  implicit val longSource: IdSource[Long] = new IdSource[Long] {
    override def random: Long = Random.nextLong()
    override def parse(str: String): Long = str.toLong
    override def empty: Long = 0L
  }

  implicit val bigIntSource: IdSource[BigInt] = new IdSource[BigInt] {
    override def random: BigInt = BigInt(16, Random)
    override def parse(str: String): BigInt = BigInt(str)
    override def empty: BigInt = BigInt(0)
  }

  implicit val stringSource: IdSource[String] = new IdSource[String] {
    override def random: String = Random.alphanumeric.take(32).mkString
    override def parse(str: String): String = str
    override def empty: String = ""
  }

}
