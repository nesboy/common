import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "dev.tcheng.common"
version = "0.0.1"

plugins {
    kotlin("jvm")
    id("io.gitlab.arturbosch.detekt")
    id("dev.tcheng.common.gradle.plugin.detekt-standards")
    id("dev.tcheng.common.gradle.plugin.unit-test")
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    val detektVersion: String by project
    val junitVersion: String by project

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")

    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:$detektVersion")
}

`detekt-standards` {
    ignoreFailures.set(true)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}
