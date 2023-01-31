package dev.tcheng.common.gradle.plugin

import dev.tcheng.common.gradle.model.DetektPresetPluginExtension
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Project

class DetektPresetPlugin : StructuredPlugin<DetektPresetPluginExtension>(
    extensionName = "detektPreset",
    extensionClass = DetektPresetPluginExtension::class.java
) {

    override fun applyPlugins(project: Project, extension: DetektPresetPluginExtension) {
        project.pluginManager.apply(DetektPlugin::class.java)
    }

    override fun configurePlugins(project: Project, extension: DetektPresetPluginExtension) {
        project.extensions.configure(DetektExtension::class.java) {
            it.apply {
                parallel = true
                ignoreFailures = extension.ignoreFailures.get()
                autoCorrect = extension.autoCorrect.get()
            }
        }
    }

    override fun configureTasks(project: Project, extension: DetektPresetPluginExtension) {
        project.tasks.withType(Detekt::class.java).configureEach {
            it.reports.apply {
                html.required.set(true)
                sarif.required.set(false)
                md.required.set(false)
                txt.required.set(false)
                xml.required.set(false)
            }
            it.jvmTarget = extension.jvmVersion.get().majorVersion
        }

        project.tasks.withType(DetektCreateBaselineTask::class.java).configureEach {
            it.jvmTarget = extension.jvmVersion.get().majorVersion
        }

        // task to copy detekt config from jar to config/detekt folder
//        project.tasks.register("copyDetektConfigToBuild", Copy::class.java) {
//            it.from() // ???
//            it.into(project.layout.projectDirectory.dir("config/detekt"))
//        }
    }
}