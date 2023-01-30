rootProject.name = "common"

include("gradle-plugin")

pluginManagement {
    val kotlinVersion: String by settings
    val detektVersion: String by settings

    plugins {
        kotlin("jvm").version(kotlinVersion)
        id("io.gitlab.arturbosch.detekt").version(detektVersion)
    }
}
