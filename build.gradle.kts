import org.jetbrains.kotlin.gradle.dsl.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.0"
    application
}

group = "me.carson"
version = "1.0"

repositories {
    mavenCentral()
}


dependencies {
    implementation("com.github.ajalt.clikt:clikt:3.5.2")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(18)
}

application {
    mainClass.set("MainKt")
}