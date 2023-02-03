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
    val jacksonVersion: String by project
    val log4jVersion: String by project
    val log4jKotlinVersion: String by project
    val junitVersion: String by project

    implementation(project(":model"))
    implementation("org.apache.logging.log4j:log4j-api:$log4jVersion")
    implementation("org.apache.logging.log4j:log4j-api-kotlin:$log4jKotlinVersion")
    implementation("tech.units:indriya:2.1.4")

    testCompileOnly("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")

    testImplementation(kotlin("test"))
    testImplementation("org.apache.logging.log4j:log4j-core:$log4jVersion")
    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
}
