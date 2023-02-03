package dev.tcheng.common.scope

import dev.tcheng.common.model.annotation.IgnoreCoverage
import dev.tcheng.common.model.exception.InternalException

@IgnoreCoverage
object ObjectManager {

    fun addObject(key: String, instance: Any) {
        val instances = ContextStorageManager.peek().objects

        if (instances.containsKey(key)) {
            throw InternalException("Context already contains Object with key=$key")
        } else {
            instances[key] = instance
        }
    }

    inline fun <reified T> getObject(key: String): T? = this.getObject(key, instanceType = T::class.java)

    fun <T> getObject(key: String, instanceType: Class<T>): T? {
        val instances = ContextStorageManager.peek().objects

        if (!instances.containsKey(key)) {
            return null
        }

        val value = instances[key]

        if (instanceType.isInstance(value)) {
            return instanceType.cast(value)
        } else {
            throw InternalException("Object with key=$key is not of type=${instanceType.simpleName}")
        }
    }

    inline fun <reified T> getRequiredObject(key: String) = this.getRequiredObject(key, T::class.java)

    fun <T> getRequiredObject(key: String, instanceType: Class<T>) = this.getObject(key, instanceType)
        ?: throw InternalException("Context does not contain Object with key=$key")

    fun getAllObjects() = ContextStorageManager.peek().objects.values
}
