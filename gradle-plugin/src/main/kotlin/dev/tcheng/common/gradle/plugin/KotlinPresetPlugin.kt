package dev.tcheng.common.gradle.plugin

import dev.tcheng.common.gradle.model.KotlinPresetPluginExtension
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class KotlinPresetPlugin : StructuredPlugin<KotlinPresetPluginExtension>(
    extensionName = "kotlinPreset",
    extensionClass = KotlinPresetPluginExtension::class.java
) {

    override fun applyPlugins(project: Project, extension: KotlinPresetPluginExtension) {
        project.pluginManager.apply(KotlinPluginWrapper::class.java)
    }

    override fun configurePlugins(project: Project, extension: KotlinPresetPluginExtension) {
        // no-op
    }

    override fun configureTasks(project: Project, extension: KotlinPresetPluginExtension) {
        project.tasks.withType(KotlinCompile::class.java) {
            it.kotlinOptions.jvmTarget = extension.jvmVersion.get().majorVersion
        }
    }
}
