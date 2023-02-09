package dev.tcheng.common.scope

import dev.tcheng.common.scope.manager.MetadataManager.addMetadata
import dev.tcheng.common.scope.manager.MetricManager.addCountMetric
import dev.tcheng.common.scope.manager.MetricManager.addMetric
import dev.tcheng.common.scope.model.Option
import dev.tcheng.common.scope.multithread.ScopeAwareExecutorService
import org.apache.logging.log4j.kotlin.Logging
import systems.uom.unicode.CLDR
import tech.units.indriya.unit.Units
import java.util.concurrent.Executors
import javax.measure.MetricPrefix

object Driver : Logging {

    @JvmStatic
    fun main(args: Array<String>) {
        val interceptor = ScopeInterceptor(
            options = setOf(Option.CONTEXT_LOG)
        )

        val threadPool = ScopeAwareExecutorService(
            delegate = Executors.newSingleThreadExecutor(),
            scopeInterceptor = interceptor
        )

        interceptor.intercept {
            addMetadata(key = "requestId", value = "request1")
            addMetadata(key = "operation", value = "abc")
            addCountMetric(key = "Items", value = 8.0)
            addMetric(key = "Test1", value = 5.0, unit = MetricPrefix.NANO(Units.SECOND))
            logger.info("main log 1")

            interceptor.intercept {
                addMetadata(key = "subRequestId", value = "request1A")
                addMetric(key = "Test2", value = 5.0, unit = MetricPrefix.GIGA(CLDR.BYTE))
                logger.info("sub log")
            }

            logger.info("main log 2")

            threadPool.submit {
                addMetadata(key = "threadRequestId", value = "ABC")
                addCountMetric(key = "Test3", value = 5.0)
                logger.info("thread log")
            }.get()

            logger.info("main log 3")
        }

        logger.info("main log 4")
        threadPool.shutdown()
    }
}
