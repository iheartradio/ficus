/* basic project info */
name := "ficus"

description := "A Scala-friendly wrapper companion for Typesafe config"

startYear := Some(2013)

licenses := Seq(
  "MIT License" -> url("http://www.opensource.org/licenses/mit-license.html")
)

/* scala versions and options */
scalaVersion := "2.11.7"

// These options will be used for *all* versions.
scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-encoding", "UTF-8"
)

scalacOptions ++= Seq(
  "-Yclosure-elim",
  "-Yinline"
)

// These language flags will be used only for 2.10.x.
// Uncomment those you need, or if you hate SIP-18, all of them.
scalacOptions <++= scalaVersion map { sv =>
  if (sv startsWith "2.10") List(
    "-Xverify",
    "-Ywarn-all",
    "-feature",
    "-language:postfixOps",
    "-language:implicitConversions",
    "-language:higherKinds"
  )
  else
    List("-target:jvm-1.8")
}

javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation")

/* dependencies */
libraryDependencies <++= scalaVersion { sv =>
  Seq(
    "org.specs2"     %% "specs2"         % "2.3.11"   % "test",
    "org.scalacheck" %% "scalacheck"     % "1.11.3"   % "test",
    "com.chuusai"    %% "shapeless"      % "2.0.0"    % "test",
    "com.typesafe"   %  "config"         % "1.3.0",
    "com.google.guava" %  "guava"         % "18.0",
    "org.scala-lang" %  "scala-reflect"  % sv         % "provided")
}

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

mappings in (Compile, packageBin) ~= { (ms: Seq[(File, String)]) =>
  ms filter { case (file, toPath) =>
      toPath != "application.conf"
  }
}

Publish.settings

