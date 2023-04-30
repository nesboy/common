package dev.tcheng.common.scope.manager

import dev.tcheng.common.scope.model.Context
import java.time.Instant

object ScopeManager {

    fun startScope() {
        val previousContext = ContextStorageManager.peekOrNull()
        ContextStorageManager.push(Context())

        previousContext?.let {
            MetadataManager.addAllMetadata(it.metadata)
        }
    }

    fun endScope() = ContextStorageManager.pop().copy(endTimestamp = Instant.now())
        .also { MetadataManager.removeMetadataFromLogger(metadataKeys = it.loggerMetadataKeys) }
}
