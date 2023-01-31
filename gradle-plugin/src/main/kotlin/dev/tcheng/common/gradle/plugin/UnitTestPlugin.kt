package dev.tcheng.common.gradle.plugin

import dev.tcheng.common.gradle.model.UnitTestPluginExtension
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestLogEvent

class UnitTestPlugin : StructuredPlugin<UnitTestPluginExtension>(
    extensionName = "unit-test-standards",
    extensionClass = UnitTestPluginExtension::class.java
) {

    override fun applyPlugins(project: Project, extension: UnitTestPluginExtension) {
        // no-op
    }

    override fun configurePlugins(project: Project, extension: UnitTestPluginExtension) {
        // no-op
    }

    override fun configureTasks(project: Project, extension: UnitTestPluginExtension) {
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
