package dev.tcheng.common.scope.model

data class Context(
    val metrics: MutableMap<String, Metric<*>> = mutableMapOf(),
    val objects: MutableMap<String, Any> = mutableMapOf(),
    val metadata: MutableMap<String, Metadata> = mutableMapOf()
)
