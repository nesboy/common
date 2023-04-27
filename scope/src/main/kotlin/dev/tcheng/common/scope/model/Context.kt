package dev.tcheng.common.scope.model

import java.time.Instant

data class Context(
    val metrics: MutableMap<String, Metric> = mutableMapOf(),
    val objects: MutableMap<String, Any> = mutableMapOf(),
    val metadata: MutableMap<String, String> = mutableMapOf(),
    val loggerMetadataKeys: MutableSet<String> = mutableSetOf(),
    val startTimestamp: Instant = Instant.now(),
    val endTimestamp: Instant? = null
)
