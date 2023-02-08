package dev.tcheng.common.scope.multithread

import dev.tcheng.common.scope.ScopeInterceptor
import dev.tcheng.common.scope.manager.MetadataManager

class ScopeAwareRunnable(
    private val scopeInterceptor: ScopeInterceptor,
    private val metadata: Map<String, String>,
    private val command: Runnable
) : Runnable {

    override fun run() {
        scopeInterceptor.intercept {
            metadata.forEach { MetadataManager.addMetadata(it.key, it.value) }
            command.run()
        }
    }
}
