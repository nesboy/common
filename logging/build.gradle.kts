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
    val log4jVersion: String by project

    compileOnly("dev.tcheng.conventions-kotlin:plugin:0.0.1")
    implementation("org.apache.logging.log4j:log4j-core:$log4jVersion")
}
