package dev.tcheng.common.gradle.plugin

import dev.tcheng.common.gradle.model.UnitTestPresetPluginExtension
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestLogEvent

class UnitTestPresetPlugin : StructuredPlugin<UnitTestPresetPluginExtension>(
    extensionName = "unitTestPreset",
    extensionClass = UnitTestPresetPluginExtension::class.java
) {

    override fun applyPlugins(project: Project, extension: UnitTestPresetPluginExtension) {
        // no-op
    }

    override fun configurePlugins(project: Project, extension: UnitTestPresetPluginExtension) {
        // no-op
    }

    override fun configureTasks(project: Project, extension: UnitTestPresetPluginExtension) {
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
