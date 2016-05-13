/* basic project info */
name := "ficus"

description := "A Scala-friendly wrapper companion for Typesafe config"

startYear := Some(2013)

/* scala versions and options */
scalaVersion := "2.11.8"

crossScalaVersions := Seq(scalaVersion.value, "2.10.6")

// These options will be used for *all* versions.
scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-unchecked",
  "-encoding", "UTF-8",
  "-Yclosure-elim",
  "-Yinline",
  "-target:jvm-1." + {
    CrossVersion.partialVersion(scalaVersion.value).collect {
      case (2, minor) if minor <= 10 => "7"
    }.getOrElse("8")
  }
)

javacOptions ++= Seq(
  "-Xlint:unchecked", "-Xlint:deprecation"
)

/* dependencies */
libraryDependencies ++= Seq(
  "org.specs2"     %% "specs2-core"       % "3.7.2"  % "test",
  "org.specs2"     %% "specs2-scalacheck" % "3.7.2"  % "test",
  "org.scalacheck" %% "scalacheck"        % "1.13.0" % "test",
  "com.chuusai"    %% "shapeless"         % "2.3.0"  % "test",
  "com.typesafe"   %  "config"            % "1.3.0",
  "org.scala-lang" %  "scala-reflect"     % scalaVersion.value % "provided",
  "org.scala-lang" % "scala-compiler"     % scalaVersion.value % "provided",
  "org.typelevel"  %% "macro-compat"      % "1.1.1",
  compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
)

/* you may need these repos */
resolvers ++= Seq(
  Resolver.sonatypeRepo("snapshots")
)

/* testing */
parallelExecution in Test := true

/* sbt behavior */
logLevel in compile := Level.Warn

traceLevel := 5

offline := false

mappings in (Compile, packageBin) := {
  val ms = mappings.in(Compile, packageBin).value
  ms filter { case (_, toPath) =>
    toPath != "application.conf"
  }
}

Publish.settings

releaseCrossBuild := true

