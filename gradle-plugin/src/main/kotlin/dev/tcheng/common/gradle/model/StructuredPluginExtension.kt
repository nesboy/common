package dev.tcheng.common.gradle.model

import org.gradle.api.JavaVersion
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import javax.inject.Inject

sealed interface StructuredPluginExtension

abstract class DetektStandardsPluginExtension @Inject constructor(
    objectFactory: ObjectFactory
) : StructuredPluginExtension {
    val jvmVersion: Property<JavaVersion> = objectFactory.property(JavaVersion::class.java)
        .convention(JavaVersion.VERSION_11)
    val ignoreFailures: Property<Boolean> = objectFactory.property(Boolean::class.java)
        .convention(false)
    val autoCorrect: Property<Boolean> = objectFactory.property(Boolean::class.java)
        .convention(true)
}

abstract class EchoPluginExtension @Inject constructor(
    objectFactory: ObjectFactory
) : StructuredPluginExtension {
    val value: Property<String> = objectFactory.property(String::class.java)
        .convention("Testing")
}

abstract class KoverStandardsPluginExtension @Inject constructor(
    objectFactory: ObjectFactory
) : StructuredPluginExtension {
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

abstract class TestSuitePluginExtension @Inject constructor(
    objectFactory: ObjectFactory
) : StructuredPluginExtension {
    val enableIntegTest: Property<Boolean> = objectFactory.property(Boolean::class.java)
        .convention(false)
}

abstract class UnitTestPluginExtension @Inject constructor(
    objectFactory: ObjectFactory
) : StructuredPluginExtension {
    val ignoreFailures: Property<Boolean> = objectFactory.property(Boolean::class.java)
        .convention(false)
}
