import sbtrelease.Version


val gc = TaskKey[Unit]("gc", "runs garbage collector")
lazy val gcTask = gc := {
  println("requesting garbage collection")
  System gc()
}

lazy val project = Project("project", file("."))
  .settings(
    /* basic project info */
    name := "ficus",
    description := "A Scala-friendly wrapper companion for Typesafe config",
    startYear := Some(2013),
    scalaVersion := "2.12.8",
    crossScalaVersions := Seq("2.10.7", "2.11.12", scalaVersion.value, "2.13.0"),
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-unchecked",
      "-encoding", "UTF-8",
      "-target:jvm-1." + {
        CrossVersion.partialVersion(scalaVersion.value).collect {
          case (2, minor) if minor <= 10  & scalaVersion.value == "2.10.7" => "8"
          case (2, minor) if minor <= 10 => "7"
        }.getOrElse("8")
      }
    ) ++ (if (scalaVersion.value.startsWith("2.11") || scalaVersion.value.startsWith("2.10")) Seq("-Yclosure-elim", "-Yinline") else Seq.empty[String]),
    javacOptions ++= Seq(
      "-Xlint:unchecked", "-Xlint:deprecation"
    ),
    unmanagedSourceDirectories in Compile ++= {
      (unmanagedSourceDirectories in Compile).value.map { dir =>
        CrossVersion.partialVersion(scalaVersion.value) match {
          case Some((2, 13)) => file(dir.getPath ++ "-2.13+")
          case _             => file(dir.getPath ++ "-2.13-")
        }
      }
    },
    libraryDependencies ++= 
      (if (scalaVersion.value.startsWith("2.10"))
         Seq(
           "org.specs2"     %% "specs2-core"       % "3.10.0" % Test,
           "org.specs2"     %% "specs2-scalacheck" % "3.10.0" % Test)
       else
         Seq(
           "org.specs2"     %% "specs2-core"       % "4.5.1" % Test,
           "org.specs2"     %% "specs2-scalacheck" % "4.5.1" % Test)) ++ 
         Seq(
           "org.scalacheck" %% "scalacheck"        % "1.14.1" % Test,
           "com.chuusai"    %% "shapeless"         % "2.3.3"  % Test,
           "com.typesafe"   %  "config"            % "1.4.0",
           "org.scala-lang" %  "scala-reflect"     % scalaVersion.value % Provided,
           "org.scala-lang" %  "scala-compiler"    % scalaVersion.value % Provided) ++
      (if (!scalaVersion.value.startsWith("2.13"))
         Seq(
           compilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full),
           "org.typelevel"  %% "macro-compat"      % "1.1.1")
       else
         Seq.empty[ModuleID]),
    resolvers ++= Seq(
      Resolver.sonatypeRepo("snapshots"),
      Resolver.bintrayRepo("iheartradio","maven"),
      Resolver.jcenterRepo
    ),
    parallelExecution in Test := true,
    /* sbt behavior */
    logLevel in compile := Level.Warn,
    traceLevel := 5,
    offline := false,
    mappings in (Compile, packageBin) := {
      val ms = mappings.in(Compile, packageBin).value
      ms filter { case (_, toPath) =>
        toPath != "application.conf"
      }
    },
    Publish.settings,
    releaseCrossBuild := true,
    mimaPreviousArtifacts :=
      Version(version.value).collect {
        case Version(major, (minor :: bugfix :: _), _) if (scalaBinaryVersion.value != "2.10") && bugfix > 0 =>
          Set(organization.value %% name.value % Seq(major, minor, bugfix - 1).mkString("."))
      }.getOrElse(Set.empty),

    gcTask
  )
