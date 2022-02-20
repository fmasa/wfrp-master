package cz.frantisekmasa.wfrp_master.inventory.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.shared.drawableResource
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardItem
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ContextMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import cz.frantisekmasa.wfrp_master.inventory.domain.Encumbrance
import cz.frantisekmasa.wfrp_master.inventory.domain.InventoryItem
import cz.frantisekmasa.wfrp_master.inventory.domain.TrappingType

@Composable
fun InventoryItemList(
    items: List<InventoryItem>,
    onClick: (InventoryItem) -> Unit,
    onRemove: (InventoryItem) -> Unit,
    onDuplicate: (InventoryItem) -> Unit,
) {
    Column {
        val strings = LocalStrings.current
        for (item in items) {
            CardItem(
                name = item.name,
                description = item.description,
                icon = { ItemIcon(trappingIcon(item.trappingType), ItemIcon.Size.Small) },
                onClick = { onClick(item) },
                contextMenuItems = listOf(
                    ContextMenu.Item(
                        strings.commonUi.buttonDuplicate,
                        onClick = { onDuplicate(item) },
                    ),
                    ContextMenu.Item(
                        strings.commonUi.buttonRemove,
                        onClick = { onRemove(item) }
                    ),
                ),
                badge = {
                    val encumbrance = item.effectiveEncumbrance

                    if (encumbrance != Encumbrance.Zero) {
                        Column(horizontalAlignment = Alignment.End) {
                            if (item.quantity > 1) {
                                Text("× ${item.quantity}")
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(Spacing.tiny)
                            ) {
                                Icon(
                                    drawableResource(Resources.Drawable.TrappingEncumbrance),
                                    strings.trappings.iconEncumbrance,
                                    Modifier.size(Spacing.medium),
                                )
                                Text(encumbrance.toString())
                            }
                        }
                    }
                }
            )
        }
    }
}

@Composable
@Stable
private fun trappingIcon(trappingType: TrappingType?) = when (trappingType) {
    is TrappingType.Ammunition -> Resources.Drawable.TrappingAmmunition
    is TrappingType.Armour -> Resources.Drawable.ArmorChest
    is TrappingType.MeleeWeapon -> Resources.Drawable.WeaponSkill
    is TrappingType.Container -> Resources.Drawable.TrappingContainer
    is TrappingType.RangedWeapon -> Resources.Drawable.BallisticSkill
    null -> Resources.Drawable.TrappingMiscellaneous
}