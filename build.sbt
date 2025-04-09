ThisBuild / tlBaseVersion := "2.3"

ThisBuild / crossScalaVersions := Seq("2.12.20", "3.3.5", "2.13.16")
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
      "org.typelevel" %%% "discipline-core" % "1.7.0",
      "org.scalatestplus" %%% "scalacheck-1-18" % "3.2.19.0",
      "org.scalatest" %%% "scalatest-funspec" % "3.2.19",
      "org.scalatest" %%% "scalatest-flatspec" % "3.2.19",
      "org.scalatest" %%% "scalatest-funsuite" % "3.2.19",
      "org.scalatest" %%% "scalatest-wordspec" % "3.2.19"
    )
  )
  .nativeSettings(
    tlVersionIntroduced := List("2.12", "2.13", "3").map(_ -> "2.3.0").toMap
  )

lazy val docs = project
  .in(file("site"))
  .settings(
    tlSiteHelium ~= {
      import laika.helium.config.*
      def textLink(p: (String, URL)) = TextLink.external(p._2.toString(), p._1)

      _.site.mainNavigation(
        appendLinks = Seq(
          ThemeNavigationSection(
            "Related Projects",
            textLink(TypelevelProject.Cats),
            textLink(TypelevelProject.Discipline)
          )
        )
      )
    }
  )
  .dependsOn(scalatest.jvm)
  .enablePlugins(TypelevelSitePlugin)
