package com.unstablebuild.idid.factory

import com.unstablebuild.idid.{Id, TypedId}
import org.scalatest.{FlatSpec, MustMatchers}

class AutoIdFactoryTest extends FlatSpec with MustMatchers {

  case class AnotherId(underlying: Int) extends TypedId[Int]

  it must "auto generate id factories when using trait" in new AutoIdFactory {

    Id.parse[AnotherId]("7") must equal (AnotherId(7))

  }

  it must "auto generate id factories when importing" in {

    import com.unstablebuild.idid.auto._
    Id.parse[AnotherId]("23") must equal (AnotherId(23))

  }

}

