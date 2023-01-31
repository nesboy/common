package dev.tcheng.common.gradle.plugin

import dev.tcheng.common.gradle.model.KoverStandardsPluginExtension
import kotlinx.kover.api.CounterType
import kotlinx.kover.api.KoverProjectConfig
import kotlinx.kover.api.VerificationTarget
import kotlinx.kover.api.VerificationValueType
import org.gradle.api.Project

class KoverStandardsPlugin : StructuredPlugin<KoverStandardsPluginExtension>(
    extensionName = "coverage-standards",
    extensionClass = KoverStandardsPluginExtension::class.java
) {

    override fun configurePlugins(project: Project, extension: KoverStandardsPluginExtension) {
        project.extensions.configure(KoverProjectConfig::class.java) { config ->
            config.apply {
                filters { filter ->
                    filter.classes {
                        it.excludes += extension.excludeClasses.get()
                    }
                }
                xmlReport {
                    it.onCheck.set(false)
                }
                htmlReport {
                    it.onCheck.set(true)
                }
                verify { config ->
                    config.apply {
                        onCheck.set(true)

                        rule { rule ->
                            rule.apply {
                                name = "LineCoverage"
                                isEnabled = extension.enableLineCoverage.get()
                                target = VerificationTarget.CLASS

                                bound {
                                    it.apply {
                                        minValue = extension.minimumLineCoveragePercentage.get()
                                        counter = CounterType.LINE
                                        valueType = VerificationValueType.COVERED_PERCENTAGE
                                    }
                                }
                            }
                        }
                        rule { rule ->
                            rule.apply {
                                name = "BranchCoverage"
                                isEnabled = extension.enableBranchCoverage.get()
                                target = VerificationTarget.CLASS

                                bound {
                                    it.apply {
                                        minValue = extension.minimumBranchCoveragePercentage.get()
                                        counter = CounterType.BRANCH
                                        valueType = VerificationValueType.COVERED_PERCENTAGE
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun configureTasks(project: Project, extension: KoverStandardsPluginExtension) {
        // no-op
    }
}
