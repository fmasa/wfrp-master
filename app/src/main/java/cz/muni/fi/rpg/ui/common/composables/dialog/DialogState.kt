package cz.muni.fi.rpg.ui.common.composables.dialog

sealed class DialogState<T> {
    class Closed<T> : DialogState<T>()
    class Opened<T>(val item: T) : DialogState<T>()
}