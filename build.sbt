Global / onChangedBuildSource := ReloadOnSourceChanges
ThisBuild / version := "0.1.0"
ThisBuild / organization := "ttg"
ThisBuild / description := "Use CLIs to create scala sources slightly more easily writing a task/plugin yourself."
ThisBuild / licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))

lazy val bintraySettings = Seq(
  bintrayRepository := "sbt-plugins",
  bintrayOrganization in bintray := None,
  bintrayReleaseOnPublish := false,
  bintrayPackageLabels := Seq("sbt", "cli", "scala", "code generator"),
  bintrayVcsUrl := Some("git:git@github.com:aappddeevv/sbt-cli-codegen"),
  publishMavenStyle := false,
)

lazy val root = (project in file("."))
  .settings(
    sbtPlugin := true,
    name := "sbt-cli-codegen",
    publishMavenStyle := false,
  )
  .settings(bintraySettings)
  .enablePlugins(GitVersioning)
