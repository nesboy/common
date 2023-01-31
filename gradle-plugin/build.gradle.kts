import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import kotlinx.kover.api.CounterType
import kotlinx.kover.api.VerificationTarget
import kotlinx.kover.api.VerificationValueType
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "dev.tcheng.common"
version = "0.0.1"

plugins {
    kotlin("jvm")
    id("io.gitlab.arturbosch.detekt")
    id("org.jetbrains.kotlinx.kover")
    `java-gradle-plugin`
}

repositories {
    mavenCentral()
}

dependencies {
    val detektVersion: String by project
    val junitVersion: String by project
    val kotlinVersion: String by project
    val koverVersion: String by project

    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:$detektVersion")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    implementation("org.jetbrains.kotlinx:kover:$koverVersion")

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")

    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:$detektVersion")
}

gradlePlugin {
    val classpathPrefix = "dev.tcheng.common.gradle.plugin"

    plugins {
        create("detektPresetPlugin") {
            id = "$classpathPrefix.detekt-preset"
            implementationClass = "$classpathPrefix.DetektPresetPlugin"
        }
        create("echoPlugin") {
            id = "$classpathPrefix.echo"
            implementationClass = "$classpathPrefix.EchoPlugin"
        }
        create("kotlinPresetPlugin") {
            id = "$classpathPrefix.kotlin-preset"
            implementationClass = "$classpathPrefix.KotlinPresetPlugin"
        }
        create("koverPresetPlugin") {
            id = "$classpathPrefix.kover-preset"
            implementationClass = "$classpathPrefix.KoverPresetPlugin"
        }
        create("testSuitePresetPlugin") {
            id = "$classpathPrefix.test-suite-preset"
            implementationClass = "$classpathPrefix.TestSuitePresetPlugin"
        }
        create("unitTestPresetPlugin") {
            id = "$classpathPrefix.unit-test-preset"
            implementationClass = "$classpathPrefix.UnitTestPresetPlugin"
        }
    }
}

detekt {
    config = files("src/main/resources/config/detekt/detekt-config.yml")
    parallel = true
    ignoreFailures = false
    autoCorrect = true
}

kover {
    isDisabled.set(false)

    filters {
        classes {
            excludes += listOf("**.model.**")
        }
    }

    xmlReport {
        onCheck.set(false)
    }

    htmlReport {
        onCheck.set(true)
    }

    verify {
        onCheck.set(true)

        rule {
            name = "LineCoverage"
            isEnabled = false
            target = VerificationTarget.CLASS

            bound {
                minValue = 90
                counter = CounterType.LINE
                valueType = VerificationValueType.COVERED_PERCENTAGE
            }
        }

        rule {
            name = "BranchCoverage"
            isEnabled = false
            target = VerificationTarget.CLASS

            bound {
                minValue = 80
                counter = CounterType.BRANCH
                valueType = VerificationValueType.COVERED_PERCENTAGE
            }
        }
    }
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
