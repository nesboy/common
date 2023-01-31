package dev.tcheng.common.gradle.plugin

import dev.tcheng.common.gradle.model.TestSuitePresetPluginExtension
import org.gradle.api.Incubating
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JvmTestSuitePlugin
import org.gradle.api.plugins.jvm.JvmTestSuite
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.testing.base.TestingExtension

@Incubating
class TestSuitePresetPlugin : StructuredPlugin<TestSuitePresetPluginExtension>(
    extensionName = "testSuitePreset",
    extensionClass = TestSuitePresetPluginExtension::class.java
) {

    override fun applyPlugins(project: Project, extension: TestSuitePresetPluginExtension) {
        project.pluginManager.apply {
            apply(JavaPlugin::class.java)
            apply(JvmTestSuitePlugin::class.java)
        }
    }

    override fun configurePlugins(project: Project, extension: TestSuitePresetPluginExtension) {
        project.extensions.configure(TestingExtension::class.java) { config ->
            config.suites.apply {
                val testSuite = named("test", JvmTestSuite::class.java)
                testSuite.configure { suite ->
                    suite.apply {
                        useJUnitJupiter()

                        targets.all { target ->
                            target.testTask.configure { task ->
                                task.apply {
                                    testLogging {
                                        it.apply {
                                            showStandardStreams = true
                                            it.events = setOf(
                                                TestLogEvent.PASSED,
                                                TestLogEvent.FAILED,
                                                TestLogEvent.SKIPPED
                                            )
                                        }
                                    }
                                    ignoreFailures = false
                                }
                            }
                        }
                    }
                }

                if (extension.enableIntegTest.get()) {
                    register("integTest", JvmTestSuite::class.java) { suite ->
                        suite.apply {
                            useJUnitJupiter()

                            dependencies {
                                it.implementation.add(it.project())
                            }
                            targets.all { target ->
                                target.testTask.configure { task ->
                                    task.apply {
                                        testLogging {
                                            it.apply {
                                                showStandardStreams = true
                                                it.events = setOf(
                                                    TestLogEvent.PASSED,
                                                    TestLogEvent.FAILED,
                                                    TestLogEvent.SKIPPED
                                                )
                                            }
                                        }
                                        ignoreFailures = false
                                        shouldRunAfter(testSuite)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun configureTasks(project: Project, extension: TestSuitePresetPluginExtension) {
        if (extension.enableIntegTest.get()) {
            project.extensions.getByType(TestingExtension::class.java).suites.named("integTest").let { suite ->
                project.tasks.named("check").configure {
                    it.dependsOn(suite.get())
                }
            }
        }
    }
}