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
    val easyRandomVersion: String by project

    implementation("org.jeasy:easy-random-core:$easyRandomVersion")
}
