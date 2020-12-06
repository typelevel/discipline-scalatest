val Scala212 = "2.12.11"

ThisBuild / crossScalaVersions := Seq("2.13.3", Scala212, "2.11.12", "3.0.0-M1")
ThisBuild / scalaVersion := Scala212

val MicrositesCond = s"matrix.scala == '$Scala212'"

ThisBuild / githubWorkflowPublishTargetBranches := Seq()

ThisBuild / githubWorkflowBuildPreamble ++= Seq(
  WorkflowStep.Use(
    "ruby",
    "setup-ruby",
    "v1",
    params = Map("ruby-version" -> "2.6"),
    cond = Some(MicrositesCond)
  ),
  WorkflowStep.Run(List("gem install sass"), cond = Some(MicrositesCond)),
  WorkflowStep.Run(List("gem install jekyll -v 4.0.0"), cond = Some(MicrositesCond))
)

ThisBuild / githubWorkflowBuild := Seq(
  WorkflowStep.Sbt(
    List("test", "scalafmtSbtCheck", "scalafmtCheckAll", "mimaReportBinaryIssues"),
    name = Some("Validate unit tests and binary compatibility")
  ),
  WorkflowStep.Sbt(List("docs/makeMicrosite"), cond = Some(MicrositesCond))
)

lazy val root = project
  .in(file("."))
  .disablePlugins(MimaPlugin)
  .settings(commonSettings, releaseSettings, skipOnPublishSettings)
  .settings(crossScalaVersions := Nil)
  .aggregate(
    scalatestJVM,
    scalatestJS
  )

lazy val scalatest = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("scalatest"))
  .settings(commonSettings, releaseSettings, mimaSettings)
  .settings(
    moduleName := "discipline-scalatest",
    libraryDependencies ++= Seq(
      "org.typelevel" %%% "discipline-core" % disciplineV,
      "org.scalatestplus" %%% "scalacheck-1-15" % "3.2.3.0",
      "org.scalatest" %%% "scalatest" % "3.2.3"
    ),
    Compile / doc / sources := {
      val old = (Compile / doc / sources).value
      if (isDotty.value)
        Seq()
      else
        old
    }
  )
  .jsSettings(
    scalaJSStage in Test := FastOptStage,
    crossScalaVersions := crossScalaVersions.value.init
  )

lazy val scalatestJVM = scalatest.jvm
lazy val scalatestJS = scalatest.js

lazy val docs = project
  .in(file("docs"))
  .disablePlugins(MimaPlugin)
  .settings(commonSettings, skipOnPublishSettings, micrositeSettings)
  .dependsOn(scalatestJVM)
  .enablePlugins(MicrositesPlugin)
  .enablePlugins(TutPlugin)

lazy val contributors = Seq(
  "larsrh" -> "Lars Hupel",
  "rossabaker" -> "Ross A. Baker",
  "travisbrown" -> "Travis Brown"
)

val disciplineV = "1.1.2"

// General Settings
lazy val commonSettings = Seq(
  organization := "org.typelevel",
  scalacOptions ++= (if (isDotty.value) Nil else Seq("-Yrangepos")),
  scalacOptions in (Compile, doc) ++= Seq(
    "-groups",
    "-sourcepath",
    (baseDirectory in LocalRootProject).value.getAbsolutePath,
    "-doc-source-url",
    "https://github.com/typelevel/discipline-scalatest/blob/v" + version.value + "€{FILE_PATH}.scala"
  )
)

lazy val releaseSettings = {
  import ReleaseTransformations._
  Seq(
    releaseCrossBuild := true,
    releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      runClean,
      runTest,
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      // For non cross-build projects, use releaseStepCommand("publishSigned")
      releaseStepCommandAndRemaining("+publishSigned"),
      setNextVersion,
      commitNextVersion,
      releaseStepCommand("sonatypeReleaseAll"),
      pushChanges
    ),
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots".at(nexus + "content/repositories/snapshots"))
      else
        Some("releases".at(nexus + "service/local/staging/deploy/maven2"))
    },
    credentials ++= (
      for {
        username <- Option(System.getenv().get("SONATYPE_USERNAME"))
        password <- Option(System.getenv().get("SONATYPE_PASSWORD"))
      } yield Credentials(
        "Sonatype Nexus Repository Manager",
        "oss.sonatype.org",
        username,
        password
      )
    ).toSeq,
    publishArtifact in Test := false,
    releasePublishArtifactsAction := PgpKeys.publishSigned.value,
    releaseVcsSign := true,
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/typelevel/discipline-scalatest"),
        "git@github.com:typelevel/discipline-scalatest.git"
      )
    ),
    homepage := Some(url("https://github.com/typelevel/discipline-scalatest")),
    licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
    publishMavenStyle := true,
    pomIncludeRepository := { _ => false },
    pomExtra := {
      <developers>
        {
        for ((username, name) <- contributors)
          yield <developer>
          <id>{username}</id>
          <name>{name}</name>
          <url>http://github.com/{username}</url>
        </developer>
      }
      </developers>
    }
  )
}

lazy val mimaSettings = {
  import sbtrelease.Version

  def semverBinCompatVersions(major: Int, minor: Int, patch: Int): Set[(Int, Int, Int)] = {
    val majorVersions: List[Int] =
      if (major == 0 && minor == 0) List.empty[Int] // If 0.0.x do not check MiMa
      else List(major)
    val minorVersions: List[Int] =
      if (major >= 1) Range(0, minor).inclusive.toList
      else List(minor)
    def patchVersions(currentMinVersion: Int): List[Int] =
      if (minor == 0 && patch == 0) List.empty[Int]
      else if (currentMinVersion != minor) List(0)
      else Range(0, patch - 1).inclusive.toList

    val versions = for {
      maj <- majorVersions
      min <- minorVersions
      pat <- patchVersions(min)
    } yield (maj, min, pat)
    versions.toSet
  }

  def mimaVersions(version: String): Set[String] =
    Version(version) match {
      case Some(Version(major, Seq(minor, patch), _)) =>
        semverBinCompatVersions(major.toInt, minor.toInt, patch.toInt)
          .map { case (maj, min, pat) => maj.toString + "." + min.toString + "." + pat.toString }
      case _ =>
        Set.empty[String]
    }
  // Safety Net For Exclusions
  lazy val excludedVersions: Set[String] = Set()

  // Safety Net for Inclusions
  lazy val extraVersions: Set[String] = Set()

  Seq(
    mimaFailOnNoPrevious := false,
    mimaFailOnProblem := mimaVersions(version.value).toList.headOption.isDefined,
    mimaPreviousArtifacts := {
      if (isDotty.value)
        Set()
      else
        (mimaVersions(version.value) ++ extraVersions)
          .filterNot(excludedVersions.contains(_))
          .map { v =>
            val moduleN = moduleName.value + "_" + scalaBinaryVersion.value.toString
            organization.value % moduleN % v
          }
    },
    mimaBinaryIssueFilters ++= {
      import com.typesafe.tools.mima.core._
      import com.typesafe.tools.mima.core.ProblemFilters._
      Seq()
    }
  )
}

lazy val micrositeSettings = {
  import microsites._
  Seq(
    micrositeName := "discipline-scalatest",
    micrositeDescription := "ScalaTest binding for Typelevel Discipline",
    micrositeGithubOwner := "typelevel",
    micrositeGithubRepo := "discipline-scalatest",
    micrositeBaseUrl := "/discipline-scalatest",
    micrositeDocumentationUrl := "https://www.javadoc.io/doc/org.typelevel/discipline-scalatest_2.12",
    micrositeGitterChannelUrl := "typelevel/discipline",
    micrositeFooterText := None,
    micrositeHighlightTheme := "atom-one-light",
    micrositePalette := Map(
      "brand-primary" -> "#3e5b95",
      "brand-secondary" -> "#294066",
      "brand-tertiary" -> "#2d5799",
      "gray-dark" -> "#49494B",
      "gray" -> "#7B7B7E",
      "gray-light" -> "#E5E5E6",
      "gray-lighter" -> "#F4F3F4",
      "white-color" -> "#FFFFFF"
    ),
    fork in tut := true,
    scalacOptions in Tut --= Seq(
      "-Xfatal-warnings",
      "-Ywarn-unused-import",
      "-Ywarn-numeric-widen",
      "-Ywarn-dead-code",
      "-Ywarn-unused:imports",
      "-Xlint:-missing-interpolator,_"
    ),
    libraryDependencies += "com.47deg" %% "github4s" % "0.20.1",
    micrositePushSiteWith := GitHub4s,
    micrositeGithubToken := sys.env.get("GITHUB_TOKEN"),
    micrositeExtraMdFiles := Map(
      file("CHANGELOG.md") -> ExtraMdFileConfig("changelog.md",
                                                "page",
                                                Map("title" -> "changelog",
                                                    "section" -> "changelog",
                                                    "position" -> "100"
                                                )
      ),
      file("CODE_OF_CONDUCT.md") -> ExtraMdFileConfig("code-of-conduct.md",
                                                      "page",
                                                      Map("title" -> "code of conduct",
                                                          "section" -> "code of conduct",
                                                          "position" -> "101"
                                                      )
      ),
      file("LICENSE") -> ExtraMdFileConfig("license.md",
                                           "page",
                                           Map("title" -> "license", "section" -> "license", "position" -> "102")
      )
    )
  )
}

lazy val skipOnPublishSettings = Seq(
  skip in publish := true,
  publish := (()),
  publishLocal := (()),
  publishArtifact := false,
  publishTo := None
)
