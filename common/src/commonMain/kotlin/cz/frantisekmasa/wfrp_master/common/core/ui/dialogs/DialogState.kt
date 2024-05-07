package cz.frantisekmasa.wfrp_master.common.core.ui.dialogs

import androidx.compose.runtime.Composable
import dev.icerock.moko.parcelize.Parcelable

sealed class DialogState<T : Parcelable?> {
    class Closed<T : Parcelable?> : DialogState<T>()

    class Opened<T : Parcelable?>(val item: T) : DialogState<T>()

    @Composable
    fun IfOpened(content: @Composable (T) -> Unit) {
        if (this is Opened) {
            content(item)
        }
    }
}
