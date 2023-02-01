group = "dev.tcheng.common"
version = "0.0.1"

plugins {
    id("common-conventions")
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    val junitVersion: String by project

    implementation(project(":model"))

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
}
