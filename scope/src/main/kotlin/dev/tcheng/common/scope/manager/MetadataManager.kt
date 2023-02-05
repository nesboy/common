package dev.tcheng.common.scope.manager

import dev.tcheng.common.model.annotation.IgnoreCoverage
import dev.tcheng.common.model.exception.InternalException
import dev.tcheng.common.scope.model.Context
import org.apache.logging.log4j.ThreadContext

@IgnoreCoverage
object MetadataManager {

    fun addMetadata(key: String, value: String) {
        val context = ContextStorageManager.peek()
        val metadata = context.metadata

        if (metadata.containsKey(key)) {
            throw InternalException("Scope already contains Metadata with key=$key")
        } else if (ThreadContext.containsKey(key)) {
            throw InternalException("Logger already contains Metadata with key=$key")
        } else {
            metadata[key] = value
            ThreadContext.put(key, value)
        }
    }

    fun getMetadataOrNull(key: String): String? = ContextStorageManager.peek().metadata[key]

    fun getMetadata(key: String) = getMetadataOrNull(key)
        ?: throw InternalException("Scope does not contain Metadata with key=$key")

    fun getAllMetadata() = ContextStorageManager.peek().metadata

    internal fun removeMetadataFromLogger(scopeContext: Context) = ThreadContext.removeAll(scopeContext.metadata.keys)
}
