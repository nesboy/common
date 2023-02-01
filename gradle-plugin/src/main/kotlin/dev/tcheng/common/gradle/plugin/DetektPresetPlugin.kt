package dev.tcheng.common.gradle.plugin

import dev.tcheng.common.gradle.model.DetektPresetPluginExtension
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.language.jvm.tasks.ProcessResources
import javax.inject.Inject

class DetektPresetPlugin @Inject constructor(
    private val objectFactory: ObjectFactory
) : StructuredPlugin<DetektPresetPluginExtension>(
    extensionName = "detektPreset",
    extensionClass = DetektPresetPluginExtension::class.java
) {

    override fun applyPlugins(project: Project, extension: DetektPresetPluginExtension) {
        project.pluginManager.apply(DetektPlugin::class.java)
    }

    override fun configurePlugins(project: Project, extension: DetektPresetPluginExtension) {
        project.dependencies.add("detektPlugins", DETEKT_FORMATTING_DEPENDENCY_NOTATION)

        project.extensions.configure(DetektExtension::class.java) {
            it.apply {
                config = objectFactory.fileCollection().from("${project.buildDir}/$DETEKT_CONFIG_PATH")
                parallel = true
                ignoreFailures = extension.ignoreFailures.get()
                autoCorrect = extension.autoCorrect.get()
            }
        }
    }

    override fun configureTasks(project: Project, extension: DetektPresetPluginExtension) {
        this.configureCopyDetektConfigTask(project)

        project.tasks.withType(Detekt::class.java).configureEach {
            it.apply {
                reports.apply {
                    html.required.set(true)
                    sarif.required.set(false)
                    md.required.set(false)
                    txt.required.set(false)
                    xml.required.set(false)
                }
                jvmTarget = extension.jvmVersion.get().majorVersion
            }
        }

        project.tasks.withType(DetektCreateBaselineTask::class.java).configureEach {
            it.jvmTarget = extension.jvmVersion.get().majorVersion
        }
    }

    private fun configureCopyDetektConfigTask(project: Project) {
        val configurationName = "detektConfig"

        with(project) {
            with(configurations.create(configurationName)) {
                isTransitive = false
            }
            dependencies.add(configurationName, PROJECT_DEPENDENCY_NOTATION)

            tasks.named("processResources", ProcessResources::class.java).configure {
                it.apply {
                    val file = project.zipTree(project.configurations.getByName(configurationName).singleFile)
                        .matching { include(DETEKT_CONFIG_PATH) }
                    from(file)
                    into(project.buildDir)
                }
            }
        }
    }

    companion object {
        // is there a better way to do this?
        const val PROJECT_DEPENDENCY_NOTATION = "dev.tcheng.common:gradle-plugin:0.0.1"
        const val DETEKT_FORMATTING_DEPENDENCY_NOTATION = "io.gitlab.arturbosch.detekt:detekt-formatting:1.22.0"

        const val DETEKT_CONFIG_PATH = "config/detekt/detekt-config.yml"
    }
}
