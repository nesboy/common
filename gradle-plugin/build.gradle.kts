import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "dev.tcheng.common"
version = "0.0.1"

plugins {
    kotlin("jvm")
    id("io.gitlab.arturbosch.detekt")
    `java-gradle-plugin`
}

repositories {
    mavenCentral()
}

dependencies {
    val detektVersion: String by project
    val junitVersion: String by project

    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:$detektVersion")

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")

    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:$detektVersion")
}

gradlePlugin {
    val classpathPrefix = "dev.tcheng.common.gradle.plugin"

    plugins {
        create("detektStandardsPlugin") {
            id = "$classpathPrefix.detekt-standards"
            implementationClass = "$classpathPrefix.DetektStandardsPlugin"
        }
        create("echoPlugin") {
            id = "$classpathPrefix.echo"
            implementationClass = "$classpathPrefix.EchoPlugin"
        }
        create("unitTestStandardsPlugin") {
            id = "$classpathPrefix.unit-test-standards"
            implementationClass = "$classpathPrefix.UnitTestStandardsPlugin"
        }
    }
}

detekt {
    config = files("src/main/resources/config/detekt/detekt-config.yml")
    parallel = true
    ignoreFailures = false
    autoCorrect = true
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

tasks.withType<Detekt>().configureEach {
    reports {
        html.required.set(true)
        txt.required.set(false)
        xml.required.set(false)
        sarif.required.set(false)
        md.required.set(false)
    }
    jvmTarget = "11"
}

tasks.withType<DetektCreateBaselineTask>().configureEach {
    jvmTarget = "11"
}

tasks.test {
    useJUnitPlatform()

    testLogging {
        showStandardStreams = true
        events = setOf(
            TestLogEvent.PASSED,
            TestLogEvent.FAILED,
            TestLogEvent.SKIPPED
        )
    }

    ignoreFailures = false
}
