package cz.frantisekmasa.wfrp_master.core.ui.forms

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.core.R
import cz.frantisekmasa.wfrp_master.core.ui.primitives.Spacing

@Composable
fun <T> SelectBox(
    label: String? = null,
    value: T,
    onValueChange: (T) -> Unit,
    items: List<Pair<T, String>>,
) {
    Column {
        var dropdownMenuExpanded by remember { mutableStateOf(false) }

        SelectBoxToggle(
            label = label,
            onClick = { dropdownMenuExpanded = !dropdownMenuExpanded },
        ) {
            val labels = remember(items) { items.toMap() }

            Text(labels.getValue(value))
        }

        DropdownMenu(
            expanded = dropdownMenuExpanded,
            toggle = {},
            onDismissRequest = { dropdownMenuExpanded = false },
            dropdownModifier = Modifier.fillMaxWidth(),
        ) {
            for ((itemValue, itemLabel) in items) {
                DropdownMenuItem(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onValueChange(itemValue)
                        dropdownMenuExpanded = false
                    },
                ) {
                    Text(itemLabel, style = MaterialTheme.typography.subtitle1)
                }
            }
        }
    }
}

@Composable
fun SelectBoxToggle(
    label: String?,
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit,
) {
    Column {
        label?.let {
            Text(
                label,
                style = MaterialTheme.typography.body2,
                modifier = Modifier.padding(bottom = 2.dp)
            )
        }

        Surface(
            shape = RoundedCornerShape(Spacing.tiny),
            border = BorderStroke(1.dp, Colors.inputBorderColor()),
            color = MaterialTheme.colors.surface,
            modifier = Modifier.clickable(onClick = onClick),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.medium),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row { content() }
                Icon(
                    vectorResource(R.drawable.ic_caret_down),
                    stringResource(R.string.icon_open_selectbox),
                )
            }
        }
    }

}
