package cz.frantisekmasa.wfrp_master.core.ui.scaffolding.tabs

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable


data class TabScreen<T>(
    @StringRes internal val tabName: Int,
    internal val content: @Composable (T) -> Unit
)
