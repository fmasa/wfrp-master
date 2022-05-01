package cz.frantisekmasa.wfrp_master.common.core.cache

import kotlin.jvm.Synchronized

class IdentityMap<K, V>(private val maxEntries: Int) {

    private var order = ArrayDeque<K>()
    private val items = mutableMapOf<K, V>()

    init {
        require(maxEntries > 0)
    }

    @Synchronized
    fun getOrPut(key: K, defaultValue: () -> V): V {
        if (items.containsKey(key)) {
            return items.getValue(key)
        }

        items[key] = defaultValue()
        order.addFirst(key)

        if (items.size > maxEntries) {
            val removedKey = order.removeLast()
            items.remove(removedKey)
        }

        return items.getValue(key)
    }
}
