import sbt.url
import sbtrelease.ReleaseStateTransformations._

val scalaV = "2.12.4"

scalacOptions += "-P:scalajs:sjsDefinedByDefault"

lazy val common = Seq(
  name := "pages",
  organization := "com.flowtick",
  scalaVersion := scalaV,
  crossScalaVersions := Seq(scalaV, "2.11.11"),
  releasePublishArtifactsAction := PgpKeys.publishSigned.value,
  releaseCrossBuild := true,
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
    releaseStepCommandAndRemaining("sonatypeReleaseAll"),
    pushChanges
  ),
  publishTo := Some(
    if (isSnapshot.value)
      Opts.resolver.sonatypeSnapshots
    else
      Opts.resolver.sonatypeStaging
  ),
  publishMavenStyle := true,
  licenses := Seq("APL2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt")),
  homepage := Some(url("https://github.com/flowtick/pages")),
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/flowtick/pages.git"),
      "scm:git@github.com:flowtick/pages.git"
    )
  ),
  developers := List(
    Developer(id = "adrobisch", name = "Andreas Drobisch", email = "github@drobisch.com", url = url("http://drobisch.com/"))
  )
)

lazy val root = project.in(file(".")).
  settings(common).
  aggregate(pagesJS).
  settings(
    publish := {},
    publishLocal := {},
    PgpKeys.publishSigned := {}
  )

lazy val pages = crossProject.in(file(".")).
  settings(common).
  settings(
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.3" % Test
  )

lazy val pagesJS = pages.js.settings(
  libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.3" % Provided,
  artifactPath in (Compile, fastOptJS) := baseDirectory.value / ".." / "dist" / "pages.js",
  artifactPath in (Compile, fullOptJS) := (artifactPath in (Compile, fastOptJS)).value
)

lazy val pagesJVM = pages.jvm