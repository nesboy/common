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
    val log4jVersion: String by project

    implementation("org.apache.logging.log4j:log4j-core:$log4jVersion")
}
