package dev.tcheng.common.scope.model

data class ContextConfig(
    val metadataTargets: Map<String, Set<Target>> = emptyMap()
)

enum class Target {
    LOG,
    METRIC
}
