name := "sample-cqrs-cargo"

version := "0.1-SNAPSHOT"

organization := "com.github.aloiscochard"

scalaVersion := "2.9.1"

scalacOptions += "-unchecked"

scalacOptions += "-deprecation"

scalacOptions += "-Ydependent-method-types"

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2" % "1.7.1" % "test"
)

resolvers ++= Seq(
  "Sonatype OSS Releases" at "http://oss.sonatype.org/content/repositories/releases/",
  "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"
)
