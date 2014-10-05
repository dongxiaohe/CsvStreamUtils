name := "CsvStreamUtils"

version := "1.03"

organization := "com.github.dannywe"

scalaVersion := "2.11.2"

pgpReadOnly := false

publishMavenStyle := true

publishArtifact in Test := false

homepage := Some(url("https://github.com/DannyWE/CsvStreamUtils"))


publishTo := {
  val nexus = "https://oss.sonatype.org/"
//  if (isSnapshot.value)
//    Some("snapshots" at nexus + "content/repositories/snapshots")
//  else
  Some("releases"  at "https://oss.sonatype.org/service/local/staging/deploy/maven2")
}

pomExtra :=
    <licenses>
      <license>
        <name>BSD-style</name>
        <url>http://opensource.org/licenses/Apache-2.0</url>
        <distribution>repo</distribution>
      </license>
    </licenses>
    <scm>
      <url>git@github.com/DannyWE/CsvStreamUtils.git</url>
      <connection>scm:git:git@github.com/DannyWE/CsvStreamUtils.git</connection>
    </scm>
    <developers>
      <developer>
        <id>danny.dong</id>
        <name>Xiaohe Dong</name>
      </developer>
    </developers>

credentials += Credentials(new File("C://repo//sona.txt"))

libraryDependencies ++= Seq(
  "net.sf.opencsv" % "opencsv" % "2.0",
  "org.scalaz" % "scalaz-core_2.11" % "7.1.0",
  "javax.validation" % "validation-api" % "1.0.0.GA",
  "org.hibernate" % "hibernate-validator" % "4.2.0.Final",
  "org.scalaz.stream" %% "scalaz-stream" % "0.5a",
  "com.google.guava" % "guava" % "17.0",
  "com.google.code.findbugs" % "jsr305" % "1.3.9"
//  ,
//  "org.slf4j" % "slf4j-log4j12" % "1.6.1",
//  "org.slf4j" % "slf4j-api" % "1.6.1",
//  "log4j" % "log4j" % "1.2.16"
)

resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"