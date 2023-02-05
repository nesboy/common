package dev.tcheng.common.scope.model

import java.time.Instant

data class Context(
    val metrics: MutableMap<String, Metric<*>> = mutableMapOf(),
    val objects: MutableMap<String, Any> = mutableMapOf(),
    val metadata: MutableMap<String, String> = mutableMapOf(),
    val startTimestamp: Instant = Instant.now(),
    val endTimestamp: Instant? = null
) {
    override fun toString() =
        "Context(metrics=$metrics, metadata=$metadata, " +
            "startTimestamp=$startTimestamp, endTimestamp=$endTimestamp)"
}
