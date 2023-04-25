package dev.tcheng.common.scope.manager

import dev.tcheng.common.model.exception.InternalException
import org.apache.logging.log4j.ThreadContext

object MetadataManager {

    fun addMetadata(key: String, value: String) {
        val context = ContextStorageManager.peek()
        val metadata = context.metadata
        val loggerMetadataKeys = context.loggerMetadataKeys

        if (metadata.containsKey(key)) {
            if (metadata[key] != value) {
                throw InternalException("Scope already contains Metadata with key=$key and a different value")
            }
        } else {
            metadata[key] = value
        }

        if (ThreadContext.containsKey(key)) {
            if (ThreadContext.get(key) != value) {
                throw InternalException("Logger already contains Metadata with key=$key and a different value")
            }
        } else {
            ThreadContext.put(key, value)
            loggerMetadataKeys.add(key)
        }
    }

    fun addAllMetadata(entries: Map<String, String>) {
        entries.forEach { this.addMetadata(it.key, it.value) }
    }

    fun getMetadataOrNull(key: String): String? = ContextStorageManager.peek().metadata[key]

    fun getMetadata(key: String) = this.getMetadataOrNull(key)
        ?: throw InternalException("Scope does not contain Metadata with key=$key")

    fun getAllMetadata() = ContextStorageManager.peek().metadata

    internal fun removeMetadataFromLogger(metadataKeys: Set<String>) = ThreadContext.removeAll(metadataKeys)
}
