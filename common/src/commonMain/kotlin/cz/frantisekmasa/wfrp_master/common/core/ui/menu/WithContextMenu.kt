package cz.frantisekmasa.wfrp_master.common.core.ui.menu

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ContextMenu
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

@Composable
fun WithContextMenu(
    items: List<ContextMenu.Item>,
    onClick: () -> Unit = {},
    toggle: @Composable () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Box(
            Modifier.combinedClickable(
                onLongClickLabel = LocalStrings.current.commonUi.labelOpenContextMenu,
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
