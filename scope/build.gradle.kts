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

    implementation(project(":model"))
    implementation(lib.log4j.api)
    implementation(lib.log4j.api.kotlin)
    implementation(lib.uom.quantity)
    implementation(lib.uom.unicode)

    testImplementation(kotlin("test"))
    testImplementation(project(":easy-random"))
    testImplementation(project(":model"))
    testImplementation(lib.mockk)
    testImplementation(lib.easyrandom)
    testImplementation(lib.junit)

    integTestCompileOnly(project(":logging"))

    integTestImplementation(kotlin("test"))
    integTestImplementation(project(":model"))
    integTestImplementation(lib.log4j.api)
    integTestImplementation(lib.log4j.api.kotlin)
    integTestImplementation(lib.log4j.core)
    integTestImplementation(lib.junit)
    integTestImplementation(lib.uom.quantity)
    integTestImplementation(lib.uom.unicode)
}

tasks.test {
    // temporary hack to mock Instant
    jvmArgs(
        "--add-opens", "java.base/java.time=ALL-UNNAMED",
        "--add-opens", "java.base/java.lang.reflect=ALL-UNNAMED"
    )
}
