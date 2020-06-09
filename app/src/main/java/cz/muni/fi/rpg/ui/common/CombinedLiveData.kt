package cz.muni.fi.rpg.ui.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer

/**
 * Combined two LiveData sources and notifies observer with Pair of non-null values from these sources
 *
 * This is especially useful when we need to compose data for UI from different sources in reactive way
 */
class CombinedLiveData<T1 : Any, T2: Any>(
    source1: LiveData<T1>,
    source2: LiveData<T2>
) : MediatorLiveData<Pair<T1, T2>>() {
    private var data1: T1? = null
    private var data2: T2? = null

    init {
        super.addSource(source1) {
            if (it == null) {
                return@addSource
            }

            data1 = it
            update()
        }

        super.addSource(source2) {
            if (it == null) {
                return@addSource
            }

            data2 = it
            update()
        }
    }

    override fun <S : Any?> addSource(source: LiveData<S>, onChanged: Observer<in S>) {
        error("Cannot add yet another source")
    }

    override fun <S : Any?> removeSource(toRemote: LiveData<S>) {
        error("Cannot remove source")
    }

    private fun update() {
        val data1 = this.data1
        val data2 = this.data2

        if (data1 != null && data2 != null) {
            value = Pair(data1, data2)
        }
    }
}