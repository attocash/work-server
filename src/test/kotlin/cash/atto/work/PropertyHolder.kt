package cash.atto.work

import java.util.concurrent.ConcurrentHashMap

object PropertyHolder {
    private val activeKeys = ConcurrentHashMap<Class<*>, String>()
    private val properties = ConcurrentHashMap<String, Any>()

    private fun setActive(
        clazz: Class<*>,
        key: String,
    ) {
        activeKeys[clazz] = key
    }

    fun add(
        key: String,
        value: Any,
    ) {
        val previousValue = properties.put(createKey(key, value), value)
        if (previousValue != null) {
            val message =
                "You can't replace an exising property. Please clear all properties or assign a different id. " +
                    "Key($key), PreviousValue($previousValue), NewValue($value)"
            throw IllegalArgumentException(message)
        }
        setActive(value.javaClass, key)
    }

    fun <T> contains(
        clazz: Class<T>,
        key: String,
    ): Boolean = properties.containsKey(createKey(clazz, key))

    operator fun <T> get(
        clazz: Class<T>,
        key: String,
    ): T {
        val value =
            properties[createKey(clazz, key)]
                ?: throw IllegalStateException("Property of type $clazz not found for the key $key")
        return clazz.cast(value)
    }

    fun getActiveKey(clazz: Class<*>): String? = activeKeys[clazz]

    operator fun <T> get(clazz: Class<T>): T {
        val activeKey = getActiveKey(clazz) ?: throw IllegalStateException("No active key for type $clazz")
        return get(clazz, activeKey)
    }

    fun getKeys(clazz: Class<*>): List<String> =
        properties
            .entries
            .asSequence()
            .filter { clazz.isInstance(it.value) }
            .map { it.key }
            .toList()

    fun <T> getAll(clazz: Class<T>): List<T> =
        properties
            .values
            .asSequence()
            .filter { clazz.isInstance(it) }
            .map { clazz.cast(it) }
            .toList()

    private fun createKey(
        clazz: Class<*>,
        key: String,
    ): String = clazz.simpleName + "-" + key

    private fun createKey(
        key: String,
        value: Any,
    ): String = createKey(value.javaClass, key)

    fun clear() {
        properties.clear()
    }
}
