import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    kotlin("jvm") version "1.9.0"
    kotlin("plugin.serialization") version "1.9.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation("io.ktor:ktor-client-java:2.3.3")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.3")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.3")

    implementation("org.jetbrains.exposed:exposed-jdbc:0.43.0")
    implementation("org.xerial:sqlite-jdbc:3.42.0.1")

    implementation("io.github.microutils:kotlin-logging:3.0.5")
    implementation("ch.qos.logback:logback-classic:1.4.11")
}

kotlin {
    target {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
            apiVersion.set(KotlinVersion.KOTLIN_1_9)
            languageVersion.set(KotlinVersion.KOTLIN_1_9)
        }
    }
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    manifest {
        attributes("Main-Class" to "blue.starry.releasechime.MainKt")
    }
}
