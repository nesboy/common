package dev.tcheng.common.scope.manager

import dev.tcheng.common.model.exception.InternalException
import dev.tcheng.common.scope.model.Metric
import dev.tcheng.common.scope.model.MetricDatapoint
import tech.units.indriya.AbstractUnit
import tech.units.indriya.quantity.Quantities
import tech.units.indriya.unit.Units
import java.time.Duration
import java.time.Instant
import javax.measure.MetricPrefix
import javax.measure.Unit
import javax.measure.quantity.Time

object MetricManager {

    fun addMetric(key: String, value: Double, unit: Unit<*>) {
        val metrics = ContextStorageManager.peek().metrics
        val existingMetric = metrics[key]

        /**
         * 3 cases
         * 1. Metric with key has not been added yet
         * 2. Metric with key exists but unit and/or aggregation differs from initial addition
         * 3. Metric with key exists and all metadata matches
         */
        if (existingMetric == null) {
            metrics[key] = Metric(
                datapoints = mutableListOf(MetricDatapoint(value)),
                unit = unit
            )
        } else if (existingMetric.unit != unit) {
            throw InternalException(
                "Metric with key=$key is already present in Scope with a " +
                    "different unit=${existingMetric.unit}"
            )
        } else {
            existingMetric.datapoints.add(MetricDatapoint(value))
        }
    }

    fun addMetric(key: String, value: Boolean, unit: Unit<*>) =
        this.addMetric(key, value = if (value) 1.0 else 0.0, unit)

    fun addCountMetric(key: String, value: Double = 1.0) =
        this.addMetric(key, value, unit = AbstractUnit.ONE)

    fun <T> addTimedMetric(
        key: String,
        unit: Unit<Time> = MetricPrefix.MILLI(Units.SECOND),
        operation: () -> T?
    ): T? {
        val startTime = Instant.now()

        try {
            return operation.invoke()
        } finally {
            val elapsedDuration = Duration.between(startTime, Instant.now())
            val normalizedElapsedDuration =
                Quantities.getQuantity(elapsedDuration.toNanos(), MetricPrefix.NANO(Units.SECOND))
            this.addMetric(key, value = normalizedElapsedDuration.to(unit).value.toDouble(), unit)
        }
    }

    fun getAllMetrics() = ContextStorageManager.peek().metrics
}
