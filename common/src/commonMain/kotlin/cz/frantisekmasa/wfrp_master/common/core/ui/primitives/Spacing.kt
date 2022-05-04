package cz.frantisekmasa.wfrp_master.common.core.ui.primitives

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object Spacing {
    val extraLarge = 24.dp
    val mediumLarge = 20.dp
    val large = 16.dp
    val medium = 12.dp
    val small = 8.dp
    val tiny = 4.dp
    val bodyPadding = extraLarge

    val bottomPaddingUnderFab = 80.dp
    val bodyPaddingWithFab = PaddingValues(
        start = bodyPadding,
        top = bodyPadding,
        end = bodyPadding,
        bottom = bottomPaddingUnderFab,
    )

    @Stable
    fun gutterSize(): Dp {
        return large
    }
}
