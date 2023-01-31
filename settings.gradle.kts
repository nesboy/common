rootProject.name = "common"

include(
    "exception"
)

includeBuild("gradle-plugin")

pluginManagement {
    val detektVersion: String by settings
    val kotlinVersion: String by settings
    val koverVersion: String by settings

    plugins {
        id("io.gitlab.arturbosch.detekt").version(detektVersion)
        kotlin("jvm").version(kotlinVersion)
        id("org.jetbrains.kotlinx.kover").version(koverVersion)
    }
}
