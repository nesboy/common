package dev.tcheng.common.scope.manager

import dev.tcheng.common.model.exception.InternalException
import dev.tcheng.common.scope.model.Context
import java.util.Deque
import java.util.LinkedList

object ContextStorageManager {
    private val stacks: ThreadLocal<Deque<Context>> = ThreadLocal.withInitial { LinkedList() }

    fun push(context: Context) {
        stacks.get().add(context)
    }

    fun pop() = stacks.get()
        .takeIf { it.isNotEmpty() }
        ?.removeLast()
        ?: throw InternalException("No Context available")

    fun peek() = this.peekOrNull() ?: throw InternalException("No Context available")

    fun peekOrNull() = stacks.get()
        .takeIf { it.isNotEmpty() }
        ?.last

    fun clear() = stacks.get().clear()
}
