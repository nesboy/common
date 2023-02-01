rootProject.name = "common"

include(
    "context",
    "model"
)

pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }

    val tchengGradlePluginVersion: String by settings

    plugins {
        listOf(
            "kotlin-preset",
            "detekt-preset",
            "test-suite-preset",
            "kover-preset"
        ).forEach { id("dev.tcheng.gradle.plugin.$it").version(tchengGradlePluginVersion) }
    }
}
