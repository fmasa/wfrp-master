package cz.frantisekmasa.wfrp_master.common.core.shared

import androidx.compose.runtime.Composable

@Composable
expect fun BackHandler(
    enabled: Boolean = true,
    onBack: () -> Unit,
)
