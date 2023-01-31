package dev.tcheng.common.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class StructuredPlugin<E>(
    private val extensionName: String,
    // Java type erasure workaround
    private val extensionClass: Class<E>
) : Plugin<Project> {

    override fun apply(project: Project) {
        project.extensions.create(extensionName, extensionClass).let {
            this.applyPlugins(project, extension = it)
            this.configurePlugins(project, extension = it)
            this.configureTasks(project, extension = it)
        }
    }

    abstract fun applyPlugins(project: Project, extension: E)
    abstract fun configurePlugins(project: Project, extension: E)
    abstract fun configureTasks(project: Project, extension: E)
}
