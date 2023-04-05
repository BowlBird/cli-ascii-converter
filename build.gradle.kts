import org.jetbrains.kotlin.gradle.dsl.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.10"
    application
}

group = "me.carson"
version = "1.0"

repositories {
    mavenCentral()
}


dependencies {
    implementation("com.github.ajalt.clikt:clikt:3.5.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.0-Beta")
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