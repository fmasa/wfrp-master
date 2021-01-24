package cz.frantisekmasa.wfrp_master.core.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.LiveData

data class LiveDataWithDefaultValue<T>(
    private val liveData: LiveData<T>,
    private val default: T
) {
    @Composable
    fun observeAsState(): State<T> = liveData.observeAsState(default)
}