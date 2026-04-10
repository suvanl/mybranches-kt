import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
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
            languageSettings {
                optIn("kotlinx.cinterop.ExperimentalForeignApi")
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

tasks.withType<Wrapper> {
    gradleVersion = "9.3.0"
    distributionType = Wrapper.DistributionType.BIN
}
