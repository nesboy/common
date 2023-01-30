package dev.tcheng.common.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class StructuredPlugin<E>(
    private val extensionName: String,
    // Java type erasure workaround
    private val extensionClass: Class<E>
) : Plugin<Project> {

    override fun apply(project: Project) {
        this.configureTasks(
            project = project,
            extension = project.extensions.create(extensionName, extensionClass)
        )
    }

    abstract fun configureTasks(project: Project, extension: E)
}
