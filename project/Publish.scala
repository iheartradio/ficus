import sbt._
import Keys._
import com.jsuereth.sbtpgp.PgpKeys
import sbtrelease.ReleasePlugin.autoImport._
import sbtrelease.ReleaseStateTransformations._


object Publish {

  pomExtra in Global := {

      <developers>
        <developer>
          <id>ceedubs</id>
          <name>Cody Allen</name>
          <email>ceedubs@gmail.com</email>
        </developer>
        <developer>
          <id>kailuowang</id>
          <name>Kailuo Wang</name>
          <email>kailuo.wang@gmail.com</email>
        </developer>
      </developers>
  }


  val publishingSettings = Seq(

    ThisBuild / organization := "com.iheart",
    publishMavenStyle := true,
    licenses := Seq("MIT" -> url("http://www.opensource.org/licenses/mit-license.html")),
    homepage := Some(url("http://iheartradio.github.io/ficus")),
    scmInfo := Some(ScmInfo(
      url("https://github.com/iheartradio/ficus"),
      "git@github.com:iheartradio/ficus.git",
      Some("git@github.com:iheartradio/ficus.git"))),
    pomIncludeRepository := { _ => false },
    Test / publishArtifact := false,
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("Snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("Releases" at nexus + "service/local/staging/deploy/maven2")
      },
    pomExtra := (
      <developers>
        <developer>
          <id>ceedubs</id>
          <name>Cody Allen</name>
          <email>ceedubs@gmail.com</email>
        </developer>
        <developer>
          <id>kailuowang</id>
          <name>Kailuo Wang</name>
          <email>kailuo.wang@gmail.com</email>
        </developer>
      </developers>
      ),
    releaseCrossBuild := true,
    releasePublishArtifactsAction := PgpKeys.publishSigned.value,

    releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      runClean,
      runTest,
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      publishArtifacts,
      setNextVersion,
      commitNextVersion,
      ReleaseStep(action = Command.process("sonatypeReleaseAll", _)),
      pushChanges
    )

  )

  val settings = publishingSettings
}
