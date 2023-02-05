package dev.tcheng.common.scope.processor

import dev.tcheng.common.scope.model.Context

interface ContextProcessor {
    fun process(context: Context)
}
