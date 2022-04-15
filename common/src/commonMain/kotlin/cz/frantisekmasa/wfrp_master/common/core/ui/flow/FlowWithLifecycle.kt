package cz.frantisekmasa.wfrp_master.common.core.ui.flow

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun <T> StateFlow<T>.collectWithLifecycle(): State<T> = collectWithLifecycle(value)

@Composable
expect fun <T> Flow<T>.collectWithLifecycle(initialValue: T): State<T>
