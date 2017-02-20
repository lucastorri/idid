lazy val commonSettings = Seq(
  version := "0.2.0",
  scalaVersion := "2.12.0",
  crossScalaVersions := Seq("2.11.8", "2.12.0"),
  organization := "com.unstablebuild",
  organizationName := "unstablebuild.com",
  homepage := Some(url("https://github.com/lucastorri/idid")),
  organizationHomepage := Some(url("http://unstablebuild.com")),
  licenses := Seq("MIT License" -> url("https://opensource.org/licenses/MIT")),
  libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % "3.0.0" % "test"
  ),
  publishMavenStyle := true,
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
    else Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  },
  publishArtifact in Test := false,
  pomIncludeRepository := (_ => false),
  pomExtra :=
    <scm>
      <url>git@github.com:lucastorri/idid.git</url>
      <connection>scm:git:git@github.com:lucastorri/idid.git</connection>
    </scm>
    <developers>
      <developer>
        <id>lucastorri</id>
        <name>Lucas Torri</name>
        <url>http://unstablebuild.com</url>
      </developer>
      <developer>
        <id>hcwilhelm</id>
        <name>Hans Christian Wilhelm</name>
        <url>https://github.com/hcwilhelm</url>
      </developer>
    </developers>
)

lazy val root = project.in(file("."))
  .settings(commonSettings: _*)
  .dependsOn(macros)
  .settings(
    name := "idid"
  )

lazy val macros = project.in(file("macros"))
  .settings(commonSettings: _*)
  .settings(
    name := "idid-macros",
    libraryDependencies ++= Seq(
      "com.typesafe" % "config" % "1.3.0",
      "org.scala-lang" % "scala-reflect" % scalaVersion.value
    )
  )
