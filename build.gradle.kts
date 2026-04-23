import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    id("org.jlleitschuh.gradle.ktlint") version "14.2.0"

    kotlin("multiplatform") version "2.3.20"
    kotlin("plugin.compose") version "2.3.20"
}

repositories {
    mavenCentral()
    google()
}

val generateBuildConfig by tasks.registering {
    val outputDir = layout.buildDirectory.dir("generated/buildconfig")
    val gitVersion: Provider<String> = providers.exec {
        commandLine("git", "describe", "--tags", "--always")
    }.standardOutput.asText.map { it.trim() }

    outputs.dir(outputDir)
    inputs.property("version", gitVersion)

    doLast {
        outputDir.get().file("BuildConfig.kt").asFile.writeText(
            """
            package com.suvanl.mybranches

            object BuildConfig {
                const val VERSION = "${gitVersion.get()}"
            }

            """.trimIndent(),
        )
    }
}

kotlin {
    macosArm64()

    targets.withType<KotlinNativeTarget>().configureEach {
        binaries {
            executable {
                baseName = "mb"
                entryPoint = "com.suvanl.mybranches.main"
            }
        }
    }

    sourceSets {
        nativeMain {
            kotlin.srcDir(generateBuildConfig)
            dependencies {
                implementation("com.jakewharton.mosaic:mosaic-runtime:0.18.0")
            }
        }
        nativeTest {
            dependencies {
                implementation(kotlin("test"))
                implementation("io.kotest:kotest-assertions-core:6.1.11")
                implementation("com.jakewharton.mosaic:mosaic-testing:0.18.0")
            }
        }
    }
}

dependencies {
    ktlintRuleset("io.nlopez.compose.rules:ktlint:0.5.7")
}

tasks.withType<Wrapper> {
    gradleVersion = "9.3.0"
    distributionType = Wrapper.DistributionType.BIN
}
