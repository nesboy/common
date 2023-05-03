package dev.tcheng.common.scope.multithread

import dev.tcheng.common.scope.ScopeInterceptor

class ScopeAwareRunnable(
    val scopeInterceptor: ScopeInterceptor,
    val parentMetadata: Map<String, String>,
    val command: Runnable
) : Runnable {

    override fun run() {
        scopeInterceptor.intercept(initialMetadata = parentMetadata) {
            command.run()
        }
    }
}
