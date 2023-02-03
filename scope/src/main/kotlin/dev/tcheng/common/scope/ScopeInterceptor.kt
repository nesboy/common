package dev.tcheng.common.scope

import dev.tcheng.common.scope.manager.MetricManager
import dev.tcheng.common.scope.manager.ScopeManager
import dev.tcheng.common.scope.model.ContextConfig
import dev.tcheng.common.scope.model.Option
import org.apache.logging.log4j.kotlin.Logging
import tech.units.indriya.AbstractUnit

class ScopeInterceptor(
    private val contextConfig: ContextConfig = ContextConfig(),
    private val options: Set<Option> = emptySet(),
    private val contextProcessors: List<ContextProcessor> = emptyList()
) : Logging {

    fun <T> intercept(isChild: Boolean = false, operation: () -> T?): T? {
        this.prepareScope(isChild)

        val result = runCatching {
            this.invokeOperation(operation)
        }.onFailure {
            this.handleThrowable(throwable = it)
        }

        this.finalizeScope()
        return result.getOrThrow()
    }

    private fun prepareScope(isChild: Boolean) {
        if (isChild) {
            ScopeManager.startChildScope(contextConfig)
        } else {
            ScopeManager.startScope(contextConfig)
        }
    }

    private fun finalizeScope() {
        ScopeManager.endScope().also {
            if (options.contains(Option.CONTEXT_LOG)) {
                logger.info("Scope ended with $it")
            }

            contextProcessors.forEach { processor ->
                processor.process(context = it, contextConfig)
            }
        }
    }

    private fun <T> invokeOperation(operation: () -> T?): T? {
        return if (options.contains(Option.OPERATION_TIME_METRIC)) {
            MetricManager.addTimedMetric(key = "Scope.OperationTime") { operation.invoke() }
        } else {
            operation.invoke()
        }
    }

    private fun handleThrowable(throwable: Throwable) {
        if (options.contains(Option.OPERATION_FAILURE_LOG)) {
            logger.error("Scope operation failed", throwable)
        }

        if (options.contains(Option.OPERATION_FAILURE_METRIC)) {
            MetricManager.addMetric(
                key = "Scope.OperationFailure.${throwable.javaClass.simpleName}",
                unit = AbstractUnit.ONE
            )
        }
    }
}
