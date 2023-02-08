package dev.tcheng.common.scope.manager

import dev.tcheng.common.model.annotation.IgnoreCoverage
import dev.tcheng.common.scope.model.Context
import java.time.Instant

@IgnoreCoverage
object ScopeManager {

    fun startScope(isChild: Boolean = false) {
        if (isChild) {
            ContextStorageManager.peek().let {
                ContextStorageManager.push(Context(metadata = it.metadata.toMutableMap()))
            }
        } else {
            ContextStorageManager.push(Context())
        }
    }

    fun endScope() = ContextStorageManager.pop().copy(endTimestamp = Instant.now())
        .also { MetadataManager.removeMetadataFromLogger(it) }
}
