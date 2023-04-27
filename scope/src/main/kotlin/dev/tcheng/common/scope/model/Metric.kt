package dev.tcheng.common.scope.model

import java.time.Instant
import javax.measure.Unit

data class Metric(
    val datapoints: MutableList<MetricDatapoint> = mutableListOf(),
    val unit: Unit<*>,
)

data class MetricDatapoint(
    val value: Double,
    val timestamp: Instant = Instant.now()
)
