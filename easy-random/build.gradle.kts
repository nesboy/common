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
    compileOnly(lib.conventions.kotlin)
    implementation(lib.easyrandom)
}
