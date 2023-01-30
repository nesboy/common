package dev.tcheng.common.gradle.plugin

import dev.tcheng.common.gradle.model.EchoPluginExtension
import dev.tcheng.common.gradle.task.EchoTask
import org.gradle.api.Project

class EchoPlugin : StructuredPlugin<EchoPluginExtension>(
    extensionName = "echo",
    extensionClass = EchoPluginExtension::class.java
) {

    override fun configureTasks(project: Project, extension: EchoPluginExtension) {
        project.tasks.register("echo", EchoTask::class.java)
    }
}
