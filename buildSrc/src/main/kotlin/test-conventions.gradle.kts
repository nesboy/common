import org.gradle.api.attributes.TestSuiteType
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    `jvm-test-suite`
}

testing {
    suites {
        configureEach {
            if (this is JvmTestSuite) {
                useJUnitJupiter()
                targets {
                    all {
                        testTask.configure {
                            testLogging {
                                showStandardStreams = true
                                events = setOf(
                                    TestLogEvent.PASSED,
                                    TestLogEvent.FAILED,
                                    TestLogEvent.SKIPPED
                                )
                            }
                        }
                    }
                }
            }
        }

        val test by getting(JvmTestSuite::class)

        val integTest by registering(JvmTestSuite::class) {
            testType.set(TestSuiteType.INTEGRATION_TEST)

            dependencies {
                implementation(project())
            }

            targets {
                all {
                    testTask.configure {
                        shouldRunAfter(test)
                    }
                }
            }
        }
    }
}

tasks.named("check") {
    dependsOn(testing.suites.named("integTest"))
}
