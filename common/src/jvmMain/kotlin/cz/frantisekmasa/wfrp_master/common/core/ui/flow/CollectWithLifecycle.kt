package cz.frantisekmasa.wfrp_master.common.core.ui.flow

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.flow.Flow

@Composable
actual fun <T> Flow<T>.collectWithLifecycle(initialValue: T): State<T> = collectAsState(initialValue)
