package cz.frantisekmasa.wfrp_master.common.core.shared

import androidx.compose.runtime.Composable

@Composable
actual fun BackHandler(
    enabled: Boolean,
    onBack: () -> Unit,
) {
    // No-op for JVM
}
