package dev.tcheng.common.scope.manager

import dev.tcheng.common.model.annotation.IgnoreCoverage
import dev.tcheng.common.scope.model.Context
import java.time.Instant

@IgnoreCoverage
object ScopeManager {

    fun startScope() {
        val previousContext = ContextStorageManager.peekOrNull()
        ContextStorageManager.push(Context())

        previousContext?.metadata?.forEach {
            MetadataManager.addMetadata(it.key, it.value)
        }
    }

    fun endScope() = ContextStorageManager.pop().copy(endTimestamp = Instant.now())
        .also { MetadataManager.removeMetadataFromLogger(metadataKeys = it.loggerMetadataKeys) }
}
