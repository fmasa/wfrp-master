package cz.muni.fi.rpg.ui.common.composables

import androidx.compose.foundation.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun TextInput(
    label: String? = null,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        label = {
            if (label != null) {
                Text(label)
            }
        }
    )
}

@Composable
fun IntegerInput(
    label: String? = null,
    value: Int,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier) {
}