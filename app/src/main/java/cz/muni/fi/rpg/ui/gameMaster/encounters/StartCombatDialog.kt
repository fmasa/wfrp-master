package cz.muni.fi.rpg.ui.gameMaster.encounters

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.Checkbox
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.core.ui.primitives.CardContainer
import cz.muni.fi.rpg.ui.common.composables.CardTitle

private fun isAtLeastOneChecked(items: Map<out Any, Boolean>) = items.containsValue(true)
private fun <T> pickCheckedOnes(items: Map<T, Boolean>): List<T> =
    items.filterValues { it }
        .keys
        .toList()

@Composable
private fun <T> CombatantList(
    @StringRes title: Int,
    items: MutableMap<T, Boolean>,
    nameFactory: (T) -> String
) {
    CardContainer(Modifier.fillMaxWidth()) {
        CardTitle(title)

        for (item in items.keys.sortedBy { nameFactory(it) }) {
            ListItem(
                icon = {
                    Checkbox(
                        checked = items[item] ?: false,
                        onCheckedChange = { items[item] = it },
                    )
                },
                modifier = Modifier.toggleable(
                    value = items[item] ?: false,
                    onValueChange = { items[item] = it },
                ),
                text = { Text(nameFactory(item)) }
            )
        }
    }
}