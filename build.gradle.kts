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

kotlin {
    macosArm64()

    targets.withType<KotlinNativeTarget>().configureEach {
        binaries {
            executable()
        }
    }

    sourceSets {
        nativeMain {
            dependencies {
                implementation("com.jakewharton.mosaic:mosaic-runtime:0.18.0")
            }
        }
        nativeTest {
            dependencies {
                implementation(kotlin("test"))
                implementation("io.kotest:kotest-assertions-core:6.1.11")
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
