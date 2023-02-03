package dev.tcheng.common.scope

import dev.tcheng.common.scope.MetadataManager.addMetadata
import dev.tcheng.common.scope.MetricManager.addMetric
import dev.tcheng.common.scope.ScopeManager.withScope
import dev.tcheng.common.scope.model.MetadataTarget
import org.apache.logging.log4j.ThreadContext
import tech.units.indriya.AbstractUnit
import tech.units.indriya.unit.Units
import javax.measure.MetricPrefix

fun main() {
    withScope {
        addMetadata(key = "requestId", value = "1234", targets = setOf(MetadataTarget.LOG))
        addMetadata(key = "operation", value = "abc", targets = setOf(MetadataTarget.METRIC))
        addMetric(key = "Order.Count", value = 5.0, unit = AbstractUnit.ONE)
        addMetric(key = "Order.Count", value = 8.0, unit = AbstractUnit.ONE)
        addMetric(key = "Some.Time", value = 5.0, unit = MetricPrefix.NANO(Units.SECOND))

        println("log4j context=${ThreadContext.getContext()}")

        withScope(isChild = true) {
            addMetadata(key = "subRequestId", value = "1234-A", targets = setOf(MetadataTarget.LOG))
            addMetric(key = "Some.Time", value = 5.0, unit = MetricPrefix.NANO(Units.SECOND))

            println("log4j context=${ThreadContext.getContext()}")
        }
    }

    println("log4j context=${ThreadContext.getContext()}")
}
