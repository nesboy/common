package dev.tcheng.common.context

import dev.tcheng.common.model.exception.InternalException

object ContextManager {
    private val context: ThreadLocal<MutableMap<Enum<*>, Any>> = ThreadLocal.withInitial { mutableMapOf() }

    fun put(key: Enum<*>, value: Any) {
        context.get()[key] = value
    }

    fun putAll(contextEntries: Map<Enum<*>, Any>) {
        context.get().putAll(contextEntries)
    }

    inline fun <reified T> get(key: Enum<*>): T? = this.get(key, valueType = T::class.java)

    fun <T> get(key: Enum<*>, valueType: Class<T>): T? {
        val contextMap = context.get()

        if (!contextMap.containsKey(key)) {
            return null
        }

        val value = contextMap[key]

        if (valueType.isInstance(value)) {
            return valueType.cast(value)
        } else {
            throw InternalException(
                "Value associated with key=${key.javaClass.simpleName}.$key is not of type=${valueType.simpleName}"
            )
        }
    }

    inline fun <reified T> getRequired(key: Enum<*>) = this.getRequired(key, T::class.java)

    fun <T> getRequired(key: Enum<*>, valueType: Class<T>) = this.get(key, valueType)
        ?: throw InternalException(
            "Value associated with key=${key.javaClass.simpleName}.$key is not present in the context map"
        )

    fun getContext() = context.get().toMap()

    fun clear() = context.get().clear()
}
