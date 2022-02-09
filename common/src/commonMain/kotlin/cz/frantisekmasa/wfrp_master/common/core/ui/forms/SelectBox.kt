package cz.frantisekmasa.wfrp_master.common.core.ui.forms

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import cz.frantisekmasa.wfrp_master.common.core.ui.menu.DropdownMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.menu.DropdownMenuItem
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

@Composable
inline fun <reified T : NamedEnum> SelectBox(
    value: T,
    noinline onValueChange: (T) -> Unit,
    items: Array<T>,
    modifier: Modifier = Modifier,
    label: String? = null,
) {
    Box(modifier) {
        val strings = LocalStrings.current

        SelectBox(
            label = label,
            value = value,
            onValueChange = onValueChange,
            items = remember(items) { items.map { it to it.nameResolver(strings) } }
        )
    }
}

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
            onDismissRequest = { dropdownMenuExpanded = false },
            modifier = Modifier.fillMaxWidth(),
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
        label?.let { SelectBoxLabel(it) }

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
                    Icons.Rounded.ExpandMore,
                    LocalStrings.current.commonUi.labelExpandSelectBox,
                )
            }
        }
    }
}
