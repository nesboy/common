import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "dev.tcheng.common"
version = "0.0.1"

plugins {
    kotlin("jvm")
    id("dev.tcheng.common.gradle.plugin.detekt-standards")
    id("dev.tcheng.common.gradle.plugin.test-suite")
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    val junitVersion: String by project

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
}

`detekt-standards` {
    ignoreFailures.set(true)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}
