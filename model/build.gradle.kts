group = "dev.tcheng.common"
version = "0.0.1"

plugins {
    listOf(
        "kotlin-preset",
        "detekt-preset"
    ).forEach { id("dev.tcheng.gradle.plugin.$it") }
    `java-library`
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
}
