package cz.muni.fi.rpg.ui.common

import androidx.annotation.ColorRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.ui.views.CharacterPoint

@Composable
fun NumberPicker(
    label: String,
    value: Int,
    @ColorRes color: Int = R.color.colorText,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    AndroidView(
        viewBlock = {
            val view = CharacterPoint(it, label, value)

            view.setIncrementListener(onIncrement)
            view.setDecrementListener(onDecrement)
            view.setColor(color)

            view
        },
        update = {
            it.value = value
            it.setLabel(label)
            it.setColor(color)

            it.setIncrementListener(onIncrement)
            it.setDecrementListener(onDecrement)
        }
    )
}