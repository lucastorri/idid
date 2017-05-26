# idid

`idid` is a common interface for different Id types. It allows you to
define distinct types for each of your Id types, even though they might
have the same backing type (`Int`, `Long`, `UUID`, etc).


## Reasoning

Imagine you have a project with several entities, and they, in some cases,
share the same base type of Id. For instance, if you have a `Customer`
entity and `Product`, both them might have Ids, and they might be an `Int`,
`Long`, or perhaps an `UUID`. For example, they might look like this:

```scala
case class Customer(id: Int, /*...*/)

case class Product(id: Int, /*...*/)
```

And perhaps, at some point, you might have a method that receives
multiple Ids. Take for example:

```scala
def isProductInBasket(customerId: Int, productId: Int) = ???
```

And that's when things start to get confusing. Simply swapping your
parameters might cause a lot of undesired headaches and debugging
sessions. And that's when you start to wonder, *"isn't Scala a typed language? How can I make it differently"*? Wouldn't you like to have different types for different Ids, even though their backing type is the same, and therefore allow the compiler to figure out that something is wrong in your code?


There are many ways to do that, but we were looking for a way that would
be generic enough that you could have a base type and easily parse
different Id types. Consider, for instance, you are using [Play](https://www.playframework.com/), and you have REST endpoints for the different entities, each having a specific Id type on the path. You don't want to write a [binder](https://www.playframework.com/documentation/2.5.x/ScalaRequestBinders) for each of them, but instead it would be much nicer to have a common one that can parse them all. The same applies for writing those Ids to the database with, let's say, [Slick](http://slick.lightbend.com/) and [custom type mappers](http://slick.lightbend.com/doc/3.1.1/userdefined.html#using-custom-scalar-types-in-queries).


## Example

The example above could be written as the following:

```scala
import com.unstablebuild.idid._

case class CustomerId(underlying: Int) extends TypedId[Int]
implicit val customerIdFactory = Id.factory[CustomerId]

case class ProductId(underlying: Int) extends TypedId[Int]
implicit val customerIdFactory = Id.factory[ProductId]

case class Customer(id: CustomerId, /*...*/)
case class Product(id: ProductId, /*...*/)

def isProductInBasket(customerId: CustomerId, productId: ProductId) = ???
```


So far, that doesn't look like much. The magic, though, starts when using
the `Id` object directly.

```scala
// Create an Id
val customerId = Id.create[CustomerId](123)

// Create random Ids
val customerId = Id.random[CustomerId]

// Parse from a String
val customerId = Id.parse[CustomerId]("123")

// Create from a default value (empty), i.e. 0
val customerId = Id.empty[CustomerId]

// Get its value
val underlyingId = Id.value(customerId)
```

This way, when you need to create something like a Play binder, you could declare
it this way:

```scala
def idBinder[T <: Id : IdFactory] = new PathBindable[T] { /*...*/ }
```


It's import to point out that you can still use your Id class normally.
We also advice to put your Id types and their factories on a
[Package Object](http://www.scala-lang.org/docu/files/packageobjects/packageobjects.html),
so they can be accessed more easily.


### Auto Generated Factories

Instead of declaring a companion variable for each of your Id classes, one can instead use the implicit factory generator provided by `com.unstablebuild.idid.factory.AutoIdFactory` or the `com.unstablebuild.idid.auto` package. For instance:

```scala
object MyIds extends AutoIdFactory {
  case class MyId(underlying: Int) extends TypedId[Int]
}

import MyIds._
val id = Id.random[MyId]
```

or 

```scala
case class MyId(underlying: Int) extends TypedId[Int]
  
import com.unstablebuild.idid.auto._
val id = Id.random[MyId]
```


### Sources

Default values for the underlying Id types, how to parse them, or how
random values are generated, are specific by instances of `IdSource`. A
source is select through an implicit binding. Default sources are defined for the following types:

* `UUID`
* `Int`
* `Long`
* `BigInt`
* `String`


If you require a type that is not available, all you have to do is declare
your own implicit source. To give an example, the source for an `Int` looks like the following

```scala
implicit val intSource: IdSource[Int] = new IdSource[Int] {
  override def random: Int = Random.nextInt()
  override def parse(str: String): Int = str.toInt
  override def empty: Int = 0
}
```

### Integrating

#### Comparator

```scala
implicit def idOrdering[T <: Id](implicit ordering: Ordering[T#UID]): Comparator[T] =
    Ordering.by[T, T#UID](_.underlying)
```

#### Play Path Binding

```scala
implicit def idBinder[T <: Id : IdFactory] = new PathBindable[T] {

  override def bind(key: String, value: String): Either[String, T] =

    Try(Id.parse[T](value)).toOption.toRight(s"Could not convert $value into ID")

  override def unbind(key: String, id: T): String =
    id.toString

}
```


#### Play Json Format

```scala
implicit def idFormat[T <: Id](implicit factory: IdFactory[T], format: Format[T#UID]): Format[T] = new Format[T] {
  override def writes(id: T): JsValue = format.writes(id.underlying)
  override def reads(json: JsValue): JsResult[T] = json.validate[T#UID].map(Id.create[T])
}
```


#### Slick Mapper

The following will create a mapper from any defined type who also has also has a valid `BaseColumnType` in Slick:

```scala
implicit def uuidMapper[T <: Id : IdFactory : ClassTag](implicit baseColumnType: BaseColumnType[T#UID]) =
    MappedColumnType.base[T, T#UID](_.underlying, Id.create[T])
```


## Install

To use it with [SBT](http://www.scala-sbt.org/), add the following to your `build.sbt` file:

```scala
resolvers += Resolver.sonatypeRepo("public")

libraryDependencies += "com.unstablebuild" %% "idid" % "0.2.0"
```


## Contributors

Special thanks to [Christian Wilhelm](https://github.com/hcwilhelm) for the ideas behind this project.


## Release

```bash
./sbt +test +macros/test
./sbt +publishSigned +macros/publishSigned
./sbt sonatypeReleaseAll
```
