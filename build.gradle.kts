import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import kotlin.script.experimental.jvm.util.KotlinJars.stdlib

plugins {
  kotlin("jvm") version "1.3.10"
  application
}

application {
  mainClassName = "br.com.astrosoft.dtw.AtivosKt"
}

repositories {
  mavenCentral()
}

dependencies {
  compile(kotlin("stdlib"))
  compile(kotlin("reflect"))

  compile("org.apache.poi:poi-ooxml:4.0.1")
  compile("com.opencsv:opencsv:4.4")
}

