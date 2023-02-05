package dev.tcheng.common.scope

import dev.tcheng.common.scope.manager.MetadataManager.addMetadata
import dev.tcheng.common.scope.manager.MetricManager.addCountMetric
import dev.tcheng.common.scope.manager.MetricManager.addMetric
import dev.tcheng.common.scope.model.Option
import org.apache.logging.log4j.ThreadContext
import systems.uom.unicode.CLDR
import tech.units.indriya.unit.Units
import java.util.EnumSet
import javax.measure.MetricPrefix

fun main() {
    val interceptor = ScopeInterceptor(
        options = EnumSet.allOf(Option::class.java)
    )

    interceptor.intercept {
        addMetadata(key = "requestId", value = "1234")
        addMetadata(key = "operation", value = "abc")
        addCountMetric(key = "Order.Count")
        addCountMetric(key = "Order.Count", value = 8.0)
        addMetric(key = "Test1", value = 5.0, unit = MetricPrefix.NANO(Units.SECOND))

        Thread.sleep(1000)

        println("log4j context=${ThreadContext.getContext()}")

        interceptor.intercept(isChild = true) {
            addMetadata(key = "subRequestId", value = "1234-A")
            addMetric(key = "Test2", value = 5.0, unit = MetricPrefix.GIGA(CLDR.BYTE))

            Thread.sleep(1000)

            println("log4j context=${ThreadContext.getContext()}")
        }
    }

    println("log4j context=${ThreadContext.getContext()}")
}
