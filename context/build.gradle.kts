group = "dev.tcheng.common"
version = "0.0.1"

plugins {
    listOf(
        "kotlin-preset",
        "detekt-preset",
        "test-suite-preset",
        "kover-preset"
    ).forEach { id("dev.tcheng.gradle.plugin.$it") }
    `java-library`
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    val junitVersion: String by project

    implementation(project(":model")) // this causes problems

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
}
