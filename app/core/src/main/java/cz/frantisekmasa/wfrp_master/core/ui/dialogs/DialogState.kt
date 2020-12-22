package cz.frantisekmasa.wfrp_master.core.ui.dialogs

sealed class DialogState<T> {
    class Closed<T> : DialogState<T>()
    class Opened<T>(val item: T) : DialogState<T>()
}