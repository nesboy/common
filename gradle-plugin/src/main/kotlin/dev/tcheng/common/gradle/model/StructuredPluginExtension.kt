package dev.tcheng.common.gradle.model

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import javax.inject.Inject

sealed class StructuredPluginExtension(project: Project) {
    val objectFactory: ObjectFactory = project.objects
}

abstract class CoverageStandardsPluginExtension @Inject constructor(
    project: Project
) : StructuredPluginExtension(project) {
    val excludeClasses: ListProperty<String> = objectFactory.listProperty(String::class.java)
        .convention(emptyList())
    val minimumLineCoveragePercentage: Property<Int> = objectFactory.property(Int::class.java)
        .convention(90)
    val minimumBranchCoveragePercentage: Property<Int> = objectFactory.property(Int::class.java)
        .convention(80)
    val enableLineCoverage: Property<Boolean> = objectFactory.property(Boolean::class.java)
        .convention(true)
    val enableBranchCoverage: Property<Boolean> = objectFactory.property(Boolean::class.java)
        .convention(true)
}

abstract class DetektStandardsPluginExtension @Inject constructor(
    project: Project
) : StructuredPluginExtension(project) {
    val jvmVersion: Property<JavaVersion> = objectFactory.property(JavaVersion::class.java)
        .convention(JavaVersion.VERSION_11)
    val ignoreFailures: Property<Boolean> = objectFactory.property(Boolean::class.java)
        .convention(false)
    val autoCorrect: Property<Boolean> = objectFactory.property(Boolean::class.java)
        .convention(true)
}

abstract class EchoPluginExtension @Inject constructor(
    project: Project
) : StructuredPluginExtension(project) {
    val value: Property<String> = objectFactory.property(String::class.java)
        .convention("Testing")
}

abstract class UnitTestStandardsPluginExtension @Inject constructor(
    project: Project
) : StructuredPluginExtension(project) {
    val ignoreFailures: Property<Boolean> = objectFactory.property(Boolean::class.java)
        .convention(false)
}
