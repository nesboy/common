package dev.tcheng.common.scope

import dev.tcheng.common.model.annotation.IgnoreCoverage
import dev.tcheng.common.scope.model.Context
import org.apache.logging.log4j.kotlin.Logging

@IgnoreCoverage
object ScopeManager : Logging {

    fun <T> withScope(isChild: Boolean = false, operation: () -> T?): T? {
        if (isChild) {
            this.startChildScope()
        } else {
            this.startScope()
        }

        try {
            return operation.invoke()
        } finally {
            this.endScope().also { logger.info(it) }
        }
    }

    fun startScope() = ContextStorageManager.push(Context())

    fun startChildScope() = ContextStorageManager.peek().let {
        ContextStorageManager.push(Context(metadata = it.metadata))
    }

    fun endScope() = ContextStorageManager.pop()
        .also { MetadataManager.removeMetadataFromLogger(it) }
}
