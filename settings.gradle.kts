rootProject.name = "common"

include(
    "easy-random",
    "logging",
    "model",
    "scope"
)

pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
}
