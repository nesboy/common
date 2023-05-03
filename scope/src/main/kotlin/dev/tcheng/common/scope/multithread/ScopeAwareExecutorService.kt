package dev.tcheng.common.scope.multithread

import dev.tcheng.common.scope.ScopeInterceptor
import dev.tcheng.common.scope.manager.MetadataManager
import java.util.concurrent.AbstractExecutorService
import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit

class ScopeAwareExecutorService(
    private val delegate: ExecutorService,
    private val scopeInterceptor: ScopeInterceptor
) : AbstractExecutorService() {

    override fun execute(command: Runnable) = delegate.execute(
        ScopeAwareRunnable(
            scopeInterceptor,
            parentMetadata = MetadataManager.getAllMetadata().toMap(),
            command
        )
    )

    override fun shutdown() = delegate.shutdown()
    override fun shutdownNow(): MutableList<Runnable> = delegate.shutdownNow()
    override fun isShutdown(): Boolean = delegate.isShutdown
    override fun isTerminated(): Boolean = delegate.isTerminated
    override fun awaitTermination(timeout: Long, unit: TimeUnit): Boolean = delegate.awaitTermination(timeout, unit)
}
