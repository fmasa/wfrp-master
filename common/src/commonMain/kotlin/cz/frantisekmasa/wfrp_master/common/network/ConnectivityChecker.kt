package cz.frantisekmasa.wfrp_master.common.network

import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.flow.Flow

interface ConnectivityChecker {
    val availability: Flow<Boolean>
}

val LocalConnectivityChecker = staticCompositionLocalOf<ConnectivityChecker> {
    error("Connectivity checker not provided")
}
