group = "dev.tcheng.common"
version = "0.0.1"

plugins {
    id("dev.tcheng.conventions-kotlin.common") version "0.0.1"
    `java-library`
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    val easyRandomVersion: String by project

    compileOnly("dev.tcheng.conventions-kotlin:plugin:0.0.1")
    implementation("org.jeasy:easy-random-core:$easyRandomVersion")
}
