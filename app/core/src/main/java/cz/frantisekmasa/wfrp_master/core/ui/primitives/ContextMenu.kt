package cz.frantisekmasa.wfrp_master.core.ui.primitives

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cz.frantisekmasa.wfrp_master.core.R.string

object ContextMenu {
    data class Item(
        val text: String,
        val onClick: () -> Unit
    )
}

@Composable
fun WithContextMenu(
    items: List<ContextMenu.Item>,
    onClick: () -> Unit = {},
    toggle: @Composable () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val onLongClickLabel = stringResource(string.open_context_menu)

    Box {
        Box(
            Modifier.combinedClickable(
                onLongClickLabel = onLongClickLabel,
                onLongClick = { expanded = true },
                onClick = onClick,
            ),
        ) {
            toggle()
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            for (item in items) {
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        item.onClick()
                    }
                ) {
                    Text(item.text, style = MaterialTheme.typography.subtitle1)
                }
            }
        }
    }
}
