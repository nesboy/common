package dev.tcheng.common.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.PluginContainer

abstract class StructuredPlugin<E>(
    private val extensionName: String,
    // Java type erasure workaround
    private val extensionClass: Class<E>
) : Plugin<Project> {

    override fun apply(project: Project) {
        this.applyPlugins(project.plugins)
        this.configureTasks(
            project = project,
            extension = project.extensions.create(extensionName, extensionClass)
        )
    }

    abstract fun applyPlugins(pluginContainer: PluginContainer)

    abstract fun configureTasks(project: Project, extension: E)
}
