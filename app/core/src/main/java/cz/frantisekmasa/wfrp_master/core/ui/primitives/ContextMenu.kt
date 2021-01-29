package cz.frantisekmasa.wfrp_master.core.ui.primitives

import androidx.compose.foundation.AmbientIndication
import androidx.compose.foundation.InteractionState
import androidx.compose.foundation.clickable
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cz.frantisekmasa.wfrp_master.core.R.*

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
    val toggleInteractionState = remember { InteractionState() }

    val onLongClickLabel = stringResource(string.open_context_menu)

    DropdownMenu(
        expanded = expanded,
        toggle = toggle,
        toggleModifier = Modifier.clickable(
            onLongClickLabel = onLongClickLabel,
            onLongClick = { expanded = true },
            onClick = onClick,
            indication = AmbientIndication.current(),
            interactionState = toggleInteractionState,
        ),
        onDismissRequest = { expanded = false },
    ) {
        for (item in items) {
            // Using key to recompose after long click is release
            // see https://issuetracker.google.com/issues/179238010
            key(toggleInteractionState.value) {
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
