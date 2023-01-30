package dev.tcheng.common.gradle.model

import org.gradle.api.provider.Property

sealed interface StructuredPluginExtension

interface EchoPluginExtension : StructuredPluginExtension {
    val value: Property<String>
}
