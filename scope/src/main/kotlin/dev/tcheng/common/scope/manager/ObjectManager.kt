package dev.tcheng.common.scope.manager

import dev.tcheng.common.model.annotation.IgnoreCoverage
import dev.tcheng.common.model.exception.InternalException

@IgnoreCoverage
object ObjectManager {

    fun addObject(key: String, instance: Any) {
        val instances = ContextStorageManager.peek().objects

        if (instances.containsKey(key)) {
            if (instances[key] != instance) {
                throw InternalException("Scope already contains Object with key=$key")
            }
        } else {
            instances[key] = instance
        }
    }

    inline fun <reified T> getObjectOrNull(key: String): T? = getObjectOrNull(key, instanceType = T::class.java)

    fun <T> getObjectOrNull(key: String, instanceType: Class<T>): T? {
        val instances = ContextStorageManager.peek().objects

        if (!instances.containsKey(key)) {
            return null
        }

        val instance = instances[key]

        if (instanceType.isInstance(instance)) {
            return instanceType.cast(instance)
        } else {
            throw InternalException("Object with key=$key is not of type=${instanceType.simpleName}")
        }
    }

    inline fun <reified T> getObject(key: String) = getObject(key, T::class.java)

    fun <T> getObject(key: String, instanceType: Class<T>) = getObjectOrNull(key, instanceType)
        ?: throw InternalException("Scope does not contain Object with key=$key")

    fun getAllObjects() = ContextStorageManager.peek().objects
}
