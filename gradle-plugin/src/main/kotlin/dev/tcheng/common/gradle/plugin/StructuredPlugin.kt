package dev.tcheng.common.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class StructuredPlugin<E>(
    private val extensionName: String,
    // Java type erasure workaround
    private val extensionClass: Class<E>
) : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.create(extensionName, extensionClass)
        this.configurePlugins(project, extension)
        this.configureTasks(project, extension)
    }

    abstract fun configurePlugins(project: Project, extension: E)
    abstract fun configureTasks(project: Project, extension: E)
}
