package dev.tcheng.common.scope

import dev.tcheng.common.scope.model.Context
import dev.tcheng.common.scope.model.ContextConfig

interface ContextProcessor {
    fun process(context: Context, contextConfig: ContextConfig)
}
