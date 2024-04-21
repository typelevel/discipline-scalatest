ThisBuild / tlBaseVersion := "2.3"

ThisBuild / crossScalaVersions := Seq("2.12.19", "3.2.1", "2.13.10")
ThisBuild / tlVersionIntroduced := Map("3" -> "2.1.5")

ThisBuild / startYear := Some(2019)
ThisBuild / licenses := Seq(License.MIT)
ThisBuild / developers := List(
  tlGitHubDev("larsrh", "Lars Hupel"),
  tlGitHubDev("rossabaker", "Ross A. Baker"),
  tlGitHubDev("travisbrown", "Travis Brown")
)

lazy val root = tlCrossRootProject.aggregate(scalatest)

lazy val scalatest = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .in(file("scalatest"))
  .settings(
    name := "discipline-scalatest",
    libraryDependencies ++= Seq(
      "org.typelevel" %%% "discipline-core" % "1.5.1",
      "org.scalatestplus" %%% "scalacheck-1-16" % "3.2.14.0",
      "org.scalatest" %%% "scalatest" % "3.2.18"
    )
  )
  .nativeSettings(
    tlVersionIntroduced := Map("2.12" -> "2.1.3", "2.13" -> "2.1.3", "3" -> "2.2.0")
  )

lazy val docs = project
  .in(file("site"))
  .settings(tlSiteRelatedProjects ++= Seq(TypelevelProject.Discipline))
  .dependsOn(scalatest.jvm)
  .enablePlugins(TypelevelSitePlugin)
