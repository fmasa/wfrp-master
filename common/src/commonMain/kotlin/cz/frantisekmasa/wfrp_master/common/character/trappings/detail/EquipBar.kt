package cz.frantisekmasa.wfrp_master.common.character.trappings.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.localizedName
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItem
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.TrappingType
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.WeaponEquip
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.SelectionDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.SubheadBar
import cz.frantisekmasa.wfrp_master.common.core.utils.launchLogged
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.Dispatchers

@Composable
fun EquipBar(
    trapping: InventoryItem,
    weapon: TrappingType.Weapon,
    onChange: suspend (InventoryItem) -> Unit,
) {
    var dialogOpened by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var saving by remember { mutableStateOf(false) }

    if (dialogOpened) {
        SelectionDialog(
            title = stringResource(Str.trappings_title_equip_weapon),
            items = listOf<WeaponEquip?>(null) + WeaponEquip.values().toList(),
            selected = weapon.equipped,
            onDismissRequest = { dialogOpened = false },
            onSelect = { equip ->
                if (saving) {
                    return@SelectionDialog
                }

                coroutineScope.launchLogged(Dispatchers.IO) {
                    try {
                        saving = true

                        onChange(
                            trapping.copy(
                                containerId = null,
                                trappingType =
                                    if (equip == null) {
                                        weapon.unequip()
                                    } else {
                                        weapon.equip(equip)
                                    },
                            ),
                        )
                    } finally {
                        saving = false
                        dialogOpened = false
                    }
                }
            },
        ) {
            Text(it?.localizedName ?: stringResource(Str.weapons_equip_not_equipped))
        }
    }

    SubheadBar {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(stringResource(Str.weapons_label_equip))

            Row(
                Modifier.clickable { dialogOpened = true },
            ) {
                Text(
                    weapon.equipped?.localizedName
                        ?: stringResource(Str.weapons_equip_not_equipped),
                )
                Icon(
                    Icons.Rounded.ExpandMore,
                    stringResource(Str.common_ui_button_edit),
                )
            }
        }
    }
}
