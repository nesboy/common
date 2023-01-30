package dev.tcheng.common.gradle.plugin

import dev.tcheng.common.gradle.model.EchoPluginExtension
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

class EchoPluginTest {

    @Test
    fun `given defined Extension, when Plugin is applied, then task should be present`() {
        // prepare
        val project = ProjectBuilder.builder().build()

        // execute
        project.pluginManager.apply("dev.tcheng.common.gradle.plugin.echo")
        project.extensions.configure(EchoPluginExtension::class.java) {
            it.value.set("Hello")
        }

        // verify
        assertNotNull(project.tasks.findByName("echo"))
    }

    @Test
    fun `given undefined Extension, when Plugin is applied, then task should be present`() {
        // prepare
        val project = ProjectBuilder.builder().build()

        // execute
        project.pluginManager.apply("dev.tcheng.common.gradle.plugin.echo")

        // verify
        assertNotNull(project.tasks.findByName("echo"))
    }
}
