package dev.tcheng.common.gradle.plugin

import dev.tcheng.common.gradle.model.UnitTestStandardsPluginExtension
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestLogEvent

class UnitTestStandardsPlugin : StructuredPlugin<UnitTestStandardsPluginExtension>(
    extensionName = "unit-test-standards",
    extensionClass = UnitTestStandardsPluginExtension::class.java
) {
    override fun configurePlugins(project: Project, extension: UnitTestStandardsPluginExtension) {
        // no-op
    }

    override fun configureTasks(project: Project, extension: UnitTestStandardsPluginExtension) {
        project.tasks.withType(Test::class.java) { task ->
            task.apply {
                useJUnitPlatform()
                testLogging {
                    it.apply {
                        showStandardStreams = true
                        events = setOf(
                            TestLogEvent.PASSED,
                            TestLogEvent.FAILED,
                            TestLogEvent.SKIPPED
                        )
                    }
                }
                ignoreFailures = extension.ignoreFailures.get()
            }
        }
    }
}
