package cz.muni.fi.rpg.ui.common.composables

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.EmphasisAmbient
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

interface Filter {
    fun process(value: String): String
}

class MaxLength(private val maxLength: Int) : Filter {
    override fun process(value: String) = if (value.length <= maxLength)
        value
    else value.substring(0, maxLength)
}

@Composable
fun TextInput(
    label: String? = null,
    value: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    filters: Array<Filter> = emptyArray(),
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = EmphasisAmbient.current.disabled.applyEmphasis(MaterialTheme.colors.onSurface)

    Column {
        label?.let { Text(label, modifier = Modifier.padding(bottom = 4.dp)) }
        TextField(
            label = {},
            modifier = modifier
                .background(MaterialTheme.colors.surface)
                .border(1.dp, borderColor, shape = MaterialTheme.shapes.small)
                .clip(MaterialTheme.shapes.small) // Hide bottom border
                .offset(y = 3.dp),                //
            value = value,
            backgroundColor = Color.Unset,
            activeColor = borderColor,
            keyboardType = keyboardType,
            inactiveColor = Color.Unset,
            onValueChange = {
                val filteredValue = filters.fold(it) { value, filter -> filter.process(value) }

                if (filteredValue != value) {
                    onValueChange(filteredValue)
                }
            },
        )
    }
}
