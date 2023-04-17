group = "dev.tcheng.common"
version = "0.0.1"

plugins {
    `java-library`
    id("common-conventions")
}

repositories {
    mavenCentral()
}

dependencies {
    val jacksonVersion: String by project
    val log4jVersion: String by project
    val log4jKotlinVersion: String by project
    val junitVersion: String by project
    val uomVersion:String by project

    implementation(project(":model"))
//    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
//    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
//    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("org.apache.logging.log4j:log4j-api:$log4jVersion")
    implementation("org.apache.logging.log4j:log4j-api-kotlin:$log4jKotlinVersion")
    implementation("systems.uom:systems-quantity:$uomVersion")
    implementation("systems.uom:systems-unicode:$uomVersion")
//    implementation("tech.uom.lib:uom-lib-jackson:$uomVersion")

    testImplementation(kotlin("test"))
    testImplementation(project(":model"))
    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")

    integTestCompileOnly(project(":logging"))
    integTestCompileOnly("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")

    integTestImplementation(kotlin("test"))
    integTestImplementation(project(":model"))
    integTestImplementation("org.apache.logging.log4j:log4j-api:$log4jVersion")
    integTestImplementation("org.apache.logging.log4j:log4j-api-kotlin:$log4jKotlinVersion")
    integTestImplementation("org.apache.logging.log4j:log4j-core:$log4jVersion")
    integTestImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
    integTestImplementation("systems.uom:systems-quantity:$uomVersion")
    integTestImplementation("systems.uom:systems-unicode:$uomVersion")
}
