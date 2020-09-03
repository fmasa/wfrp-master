package cz.muni.fi.rpg.ui.common.composables

import androidx.compose.foundation.Text
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable

object ContextMenu {
    data class Item(
        val text: String,
        val onClick: () -> Unit
    )
}

@Composable
fun ContextMenu(items: List<ContextMenu.Item>, onDismissRequest: () -> Unit, expanded: Boolean) {
    DropdownMenu(expanded = expanded, toggle = {}, onDismissRequest = onDismissRequest) {
        for (item in items) {
            DropdownMenuItem(onClick = {
                onDismissRequest()
                item.onClick()
            }) {
                Text(item.text, style = MaterialTheme.typography.subtitle1)
            }
        }
    }
}