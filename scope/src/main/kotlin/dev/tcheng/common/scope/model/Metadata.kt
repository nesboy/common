package dev.tcheng.common.scope.model

data class Metadata(
    val value: String,
    val targets: Set<MetadataTarget> = setOf()
)

enum class MetadataTarget {
    LOG,
    METRIC
}
