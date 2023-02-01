import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import kotlinx.kover.api.CounterType
import kotlinx.kover.api.VerificationTarget
import kotlinx.kover.api.VerificationValueType
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    `java-library`
    id("io.gitlab.arturbosch.detekt")
    id("org.jetbrains.kotlinx.kover")
}

repositories {
    mavenCentral()
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.22.0")
}

java {
    withSourcesJar()
}

detekt {
    config = files("${rootDir}/buildSrc/src/main/resources/detekt-config.yml")
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
