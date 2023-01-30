import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask

group = "dev.tcheng.common"
version = "1.0.0"

plugins {
    kotlin("jvm") version "1.8.0"
    id("io.gitlab.arturbosch.detekt").version("1.22.0")
    `java-gradle-plugin`
}

repositories {
    mavenCentral()
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:${project.findProperty("detekt.formatting.version")}")
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

detekt {
    parallel = true
    ignoreFailures = false
    autoCorrect = true
}

tasks.withType<Detekt>().configureEach {
    reports {
        html.required.set(true)
        txt.required.set(false)
        xml.required.set(false)
        sarif.required.set(false)
        md.required.set(false)
    }
}

tasks.withType<Detekt>().configureEach {
    jvmTarget = "11"
}
tasks.withType<DetektCreateBaselineTask>().configureEach {
    jvmTarget = "11"
}
