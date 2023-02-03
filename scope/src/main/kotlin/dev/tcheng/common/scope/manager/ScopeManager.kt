package dev.tcheng.common.scope.manager

import dev.tcheng.common.model.annotation.IgnoreCoverage
import dev.tcheng.common.scope.model.Context
import dev.tcheng.common.scope.model.ContextConfig
import java.time.Instant

@IgnoreCoverage
object ScopeManager {

    fun startScope(contextConfig: ContextConfig) = ContextStorageManager.push(Context(contextConfig))

    fun startChildScope(contextConfig: ContextConfig) = ContextStorageManager.peek().let {
        ContextStorageManager.push(Context(contextConfig, metadata = it.metadata))
    }

    fun endScope() = ContextStorageManager.pop().copy(endTimestamp = Instant.now())
        .also { MetadataManager.removeMetadataFromLogger(it) }
}
