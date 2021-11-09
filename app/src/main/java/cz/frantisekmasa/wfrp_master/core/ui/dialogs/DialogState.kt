package cz.frantisekmasa.wfrp_master.core.ui.dialogs

import android.os.Parcelable
import androidx.compose.runtime.Composable

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
