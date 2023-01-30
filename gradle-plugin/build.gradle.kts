import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "dev.tcheng.common"
version = "1.0.0"

plugins {
    kotlin("jvm") version "1.8.0"
    `java-gradle-plugin`
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:${project.findProperty("junit.version")}")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

gradlePlugin {
    plugins {
        create("echoPlugin") {
            id = "dev.tcheng.common.gradle.plugin.echo"
            implementationClass = "dev.tcheng.common.gradle.plugin.EchoPlugin"
        }
    }
}
