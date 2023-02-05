package dev.tcheng.common.scope

import dev.tcheng.common.model.annotation.IgnoreCoverage
import dev.tcheng.common.scope.manager.MetricManager
import dev.tcheng.common.scope.manager.ScopeManager
import dev.tcheng.common.scope.model.Option
import dev.tcheng.common.scope.processor.ContextProcessor
import org.apache.logging.log4j.kotlin.Logging

@IgnoreCoverage
class ScopeInterceptor(
    private val options: Set<Option> = emptySet(),
    private val contextProcessors: List<ContextProcessor> = emptyList()
) : Logging {

    fun <T> intercept(
        isChild: Boolean = false,
        optionOverrides: Set<Option>? = null,
        operation: () -> T?
    ): T? {
        ScopeManager.startScope(isChild)
        val options = optionOverrides ?: options

        val result = runCatching {
            this.handleOperation(options, operation)
        }.onFailure {
            this.handleThrowable(options, throwable = it)
        }

        this.finalizeScope(options)
        return result.getOrThrow()
    }

    private fun finalizeScope(options: Set<Option>) {
        ScopeManager.endScope().also {
            if (options.contains(Option.CONTEXT_LOG)) {
                logger.info("Scope ended with $it")
            }

            contextProcessors.forEach { processor -> processor.process(context = it) }
        }
    }

    private fun <T> handleOperation(options: Set<Option>, operation: () -> T?): T? {
        return if (options.contains(Option.OPERATION_TIME_METRIC)) {
            MetricManager.addTimedMetric(key = "Scope.OperationTime") { operation.invoke() }
        } else {
            operation.invoke()
        }
    }

    private fun handleThrowable(options: Set<Option>, throwable: Throwable) {
        if (options.contains(Option.OPERATION_FAILURE_LOG)) {
            logger.error("Scope operation failed", throwable)
        }

        if (options.contains(Option.OPERATION_FAILURE_METRIC)) {
            MetricManager.addCountMetric("Scope.OperationFailure.${throwable.javaClass.simpleName}")
        }

        val cause = throwable.cause

        if (options.contains(Option.OPERATION_FAILURE_CAUSE_METRIC) && cause != null) {
            MetricManager.addCountMetric("Scope.OperationFailureCause.${cause.javaClass.simpleName}")
        }
    }
}
