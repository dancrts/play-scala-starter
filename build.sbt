name := """play-starter"""
organization := "qrsof.com"
version := "1.0-SNAPSHOT"
scalaVersion := "2.13.14"


lazy val qrsofDependencies = Seq(
    "com.qrsof.core.api" %% "swagger" % "1.0.0-01-SNAPSHOT",
    "com.qrsof.core" %% "app-models" % "1.0.0-06-SNAPSHOT",
    "com.qrsof.core" %% "json-jackson" % "1.0.0-05-SNAPSHOT",
    "com.qrsof.core.storage" % "storage-digital-ocean" % "2.0.0-00-SNAPSHOT",
    "com.qrsof.libs.storage" % "storage-file-system" % "2.0.0-00-SNAPSHOT",
)

lazy val qrsofResolver = Seq(
    resolvers += "Nexus Releases" at "https://nexus-ci.qrsof.com/repository/maven-public",
    credentials += Credentials("Sonatype Nexus Repository Manager", "nexus-ci.qrsof.com", "deployment", "4jDzLGNHgaWiWFj")
)

lazy val thirdPartyLibraries = Seq(
    "net.codingwell" %% "scala-guice" % "6.0.0",
    "org.playframework" %% "play-slick" % "6.1.0",
    "org.playframework" %% "play-slick-evolutions" % "6.1.0",
    "org.mindrot" % "jbcrypt" % "0.4",
    "com.github.jwt-scala" %% "jwt-core" % "10.0.1",
    "com.github.jwt-scala" %% "jwt-play-json" % "10.0.1",
)

lazy val testLibraries = Seq(
    "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)
    .settings(
        libraryDependencies += guice,
        libraryDependencies ++= qrsofDependencies ++ thirdPartyLibraries ++ testLibraries,
        qrsofResolver
    )



