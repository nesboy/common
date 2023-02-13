package dev.tcheng.common.scope.multithread

import dev.tcheng.common.scope.ScopeInterceptor

class ScopeAwareRunnable(
    private val scopeInterceptor: ScopeInterceptor,
    private val parentMetadata: Map<String, String>,
    private val command: Runnable
) : Runnable {

    override fun run() {
        scopeInterceptor.intercept(initialMetadata = parentMetadata) {
            command.run()
        }
    }
}
