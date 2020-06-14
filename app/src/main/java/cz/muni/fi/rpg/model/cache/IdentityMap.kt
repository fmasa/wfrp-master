package cz.muni.fi.rpg.model.cache

import kotlin.collections.LinkedHashMap

class IdentityMap<K,V>(private val maxEntries: Int) : LinkedHashMap<K, V>() {
    init {
        require(maxEntries > 0)
    }

    override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, V>?): Boolean {
        return size > maxEntries
    }
}