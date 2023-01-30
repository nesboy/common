package dev.tcheng.common.gradle.model

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

sealed class StructuredPluginExtension(project: Project) {
    val objectFactory: ObjectFactory = project.objects
}

abstract class EchoPluginExtension @Inject constructor(
    project: Project
) : StructuredPluginExtension(project) {
    val value: Property<String> = objectFactory.property(String::class.java)
        .apply { set("Testing") }
}

abstract class DetektStandardsPluginExtension @Inject constructor(
    project: Project
) : StructuredPluginExtension(project) {
    val jvmVersion: Property<JavaVersion> = objectFactory.property(JavaVersion::class.java)
        .apply { set(JavaVersion.VERSION_11) }
    val ignoreFailures: Property<Boolean> = objectFactory.property(Boolean::class.java)
        .apply { set(false) }
    val autoCorrect: Property<Boolean> = objectFactory.property(Boolean::class.java)
        .apply { set(true) }
}