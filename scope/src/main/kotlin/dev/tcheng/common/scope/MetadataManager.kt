package dev.tcheng.common.scope

import dev.tcheng.common.model.annotation.IgnoreCoverage
import dev.tcheng.common.model.exception.InternalException
import dev.tcheng.common.scope.model.Context
import dev.tcheng.common.scope.model.Metadata
import dev.tcheng.common.scope.model.MetadataTarget
import org.apache.logging.log4j.ThreadContext

@IgnoreCoverage
object MetadataManager {

    fun addMetadata(key: String, value: String, targets: Set<MetadataTarget> = emptySet()) {
        val metadata = ContextStorageManager.peek().metadata

        if (metadata.containsKey(key)) {
            throw InternalException("Context already contains Property with key=$key")
        } else {
            metadata[key] = Metadata(value, targets)

            if (targets.contains(MetadataTarget.LOG)) {
                ThreadContext.put(key, value)
            }
        }
    }

    fun getMetadata(key: String): Metadata? = ContextStorageManager.peek().metadata[key]

    fun getRequiredMetadata(key: String) = this.getMetadata(key)
        ?: throw InternalException("Context does not contain Metadata with key=$key")

    fun getAllMetadata() = ContextStorageManager.peek().metadata

    internal fun removeMetadataFromLogger(scopeContext: Context) {
        val logKeys = scopeContext.metadata.entries
            .filter { it.value.targets.contains(MetadataTarget.LOG) }
            .map { it.key }

        ThreadContext.removeAll(logKeys)
    }
}
