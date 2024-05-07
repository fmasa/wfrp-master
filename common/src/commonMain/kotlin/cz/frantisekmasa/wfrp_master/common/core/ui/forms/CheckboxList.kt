package cz.frantisekmasa.wfrp_master.common.core.ui.forms

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState

@Composable
fun <T> CheckboxList(
    items: Array<T>,
    text: @Composable (T) -> String,
    selected: MutableState<Set<T>>,
) {
    Column {
        items.forEach { item ->
            CheckboxWithText(
                text = text(item),
                checked = item in selected.value,
                onCheckedChange = { checked ->
                    if (checked) {
                        selected.value += item
                    } else {
                        selected.value -= item
                    }
                },
            )
        }
    }
}
