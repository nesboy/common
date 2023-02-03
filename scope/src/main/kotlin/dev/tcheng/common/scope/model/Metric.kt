package dev.tcheng.common.scope.model

import java.time.Instant
import javax.measure.Quantity
import javax.measure.Unit

data class Metric<Q : Quantity<Q>>(
    val datapoints: MutableList<MetricDatapoint> = mutableListOf(),
    val unit: Unit<Q>,
    val aggregation: MetricAggregation
)

data class MetricDatapoint(
    val value: Double, // or Number?
    val timestamp: Instant = Instant.now()
)

enum class MetricAggregation {
    AVERAGE,
    DISCRETE,
    MAX,
    MIN,
    SUM
}
