package dev.tcheng.common.scope.manager

import dev.tcheng.common.model.annotation.IgnoreCoverage
import dev.tcheng.common.model.exception.InternalException
import dev.tcheng.common.scope.model.Context
import java.util.Deque
import java.util.LinkedList

@IgnoreCoverage
object ContextStorageManager {
    private val stacks: ThreadLocal<Deque<Context>> = ThreadLocal.withInitial { LinkedList() }

    fun push(context: Context) {
        stacks.get().add(context)
    }

    fun pop() = stacks.get()
        .takeIf { it.isNotEmpty() }
        ?.removeLast()
        ?: throw InternalException("No Context available")

    fun peek() = stacks.get()
        .takeIf { it.isNotEmpty() }
        ?.last
        ?: throw InternalException("No Context available")

    fun clear() = stacks.get().clear()
}
