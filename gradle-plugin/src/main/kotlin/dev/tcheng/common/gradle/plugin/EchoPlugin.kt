package dev.tcheng.common.gradle.plugin

import dev.tcheng.common.gradle.model.EchoPluginExtension
import dev.tcheng.common.gradle.task.EchoTask
import org.gradle.api.Project
import org.gradle.api.plugins.PluginContainer

class EchoPlugin : StructuredPlugin<EchoPluginExtension>(
    extensionName = "echo",
    extensionClass = EchoPluginExtension::class.java
) {

    override fun applyPlugins(pluginContainer: PluginContainer) {}


    override fun configureTasks(project: Project, extension: EchoPluginExtension) {
        project.tasks.register("echo", EchoTask::class.java)
    }
}
