package cz.frantisekmasa.wfrp_master.core.ui.scaffolding.tabs

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable


@Immutable
data class TabScreen<T>(
    @StringRes internal val tabName: Int,
    val content: @Composable (T) -> Unit
)
