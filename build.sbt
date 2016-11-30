import sbtrelease.Version

/* basic project info */
name := "ficus"

description := "A Scala-friendly wrapper companion for Typesafe config"

startYear := Some(2013)

/* scala versions and options */
scalaVersion := "2.11.8"

crossScalaVersions := Seq(scalaVersion.value, "2.10.6", "2.12.0")

// These options will be used for *all* versions.
scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-unchecked",
  "-encoding", "UTF-8",
  "-target:jvm-1." + {
    CrossVersion.partialVersion(scalaVersion.value).collect {
      case (2, minor) if minor <= 10 => "7"
    }.getOrElse("8")
  }
) ++ (if (scalaVersion.value.startsWith("2.11") || scalaVersion.value.startsWith("2.10")) Seq("-Yclosure-elim", "-Yinline") else Seq.empty[String])

javacOptions ++= Seq(
  "-Xlint:unchecked", "-Xlint:deprecation"
)

/* dependencies */
libraryDependencies ++= Seq(
  "org.specs2"     %% "specs2-core"       % "3.8.6"  % "test",
  "org.specs2"     %% "specs2-scalacheck" % "3.8.6"  % "test",
  "org.scalacheck" %% "scalacheck"        % "1.13.4" % "test",
  "com.chuusai"    %% "shapeless"         % "2.3.2"  % "test",
  "com.typesafe"   %  "config"            % "1.3.1",
  "org.scala-lang" %  "scala-reflect"     % scalaVersion.value % "provided",
  "org.scala-lang" % "scala-compiler"     % scalaVersion.value % "provided",
  "org.typelevel"  %% "macro-compat"      % "1.1.1",
  compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
)

resolvers ++= Seq(
  Resolver.sonatypeRepo("snapshots"),
  Resolver.bintrayRepo("iheartradio","maven"),
  Resolver.jcenterRepo
)

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

mimaPreviousArtifacts := (if (scalaBinaryVersion.value != "2.10") {
  Version(version.value).map {
    case Version(major, subversions, _) =>
      val (minor :: bugfix :: _) = subversions.toList
      Set(organization.value %% name.value % Seq(major, minor, bugfix - 1).mkString("."))
  }.getOrElse(Set.empty)
} else Set.empty)

