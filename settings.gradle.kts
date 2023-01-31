rootProject.name = "common"

include(
    "exception"
)

includeBuild("gradle-plugin")

pluginManagement {
    val kotlinVersion: String by settings

    plugins {
        kotlin("jvm").version(kotlinVersion)
    }
}
