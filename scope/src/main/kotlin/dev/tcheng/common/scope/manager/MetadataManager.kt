package dev.tcheng.common.scope.manager

import dev.tcheng.common.model.annotation.IgnoreCoverage
import dev.tcheng.common.model.exception.InternalException
import dev.tcheng.common.scope.model.Context
import dev.tcheng.common.scope.model.Target
import org.apache.logging.log4j.ThreadContext

@IgnoreCoverage
object MetadataManager {

    fun addMetadata(key: String, value: String) {
        val context = ContextStorageManager.peek()
        val metadata = context.metadata

        if (metadata.containsKey(key)) {
            throw InternalException("Scope already contains Property with key=$key")
        } else {
            metadata[key] = value
            val targets = context.config.metadataTargets[key]

            if (targets != null && targets.contains(Target.LOG)) {
                ThreadContext.put(key, value)
            }
        }
    }

    fun getMetadataOrNull(key: String): String? = ContextStorageManager.peek().metadata[key]

    fun getMetadata(key: String) = getMetadataOrNull(key)
        ?: throw InternalException("Scope does not contain Metadata with key=$key")

    fun getAllMetadata() = ContextStorageManager.peek().metadata

    internal fun removeMetadataFromLogger(scopeContext: Context) {
        val logKeys = scopeContext.config.metadataTargets.entries
            .filter { it.value.contains(Target.LOG) }
            .map { it.key }

        ThreadContext.removeAll(logKeys)
    }
}
