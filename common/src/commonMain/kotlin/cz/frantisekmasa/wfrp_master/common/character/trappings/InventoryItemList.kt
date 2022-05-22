package cz.frantisekmasa.wfrp_master.common.character.trappings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Encumbrance
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItem
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.TrappingType
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.shared.drawableResource
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardItem
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ContextMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

@Composable
fun InventoryItemList(
    items: List<InventoryItem>,
    onClick: (InventoryItem) -> Unit,
    onRemove: (InventoryItem) -> Unit,
    onDuplicate: (InventoryItem) -> Unit,
) {
    Column {
        for (item in items) {
            key(item.id) {
                TrappingItem(
                    trapping = item,
                    onClick = { onClick(item) },
                    onRemove = { onRemove(item) },
                    onDuplicate = { onDuplicate(item) },
                )
            }
        }
    }
}

@Composable
private fun TrappingItem(
    trapping: InventoryItem,
    onClick: () -> Unit,
    onDuplicate: () -> Unit,
    onRemove: () -> Unit,
) {
    val strings = LocalStrings.current

    CardItem(
        name = trapping.name,
        description = trapping.description,
        icon = { ItemIcon(trappingIcon(trapping.trappingType), ItemIcon.Size.Small) },
        onClick = onClick,
        contextMenuItems = listOf(
            ContextMenu.Item(
                strings.commonUi.buttonDuplicate,
                onClick = onDuplicate,
            ),
            ContextMenu.Item(
                strings.commonUi.buttonRemove,
                onClick = onRemove,
            ),
        ),
        badge = {
            val encumbrance = trapping.effectiveEncumbrance

            if (encumbrance != Encumbrance.Zero) {
                Column(horizontalAlignment = Alignment.End) {
                    if (trapping.quantity > 1) {
                        Text("× ${trapping.quantity}")
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

@Composable
@Stable
fun trappingIcon(trappingType: TrappingType?) = when (trappingType) {
    is TrappingType.Ammunition -> Resources.Drawable.TrappingAmmunition
    is TrappingType.Armour -> Resources.Drawable.ArmorChest
    is TrappingType.MeleeWeapon -> Resources.Drawable.WeaponSkill
    is TrappingType.Container -> Resources.Drawable.TrappingContainer
    is TrappingType.RangedWeapon -> Resources.Drawable.BallisticSkill
    null -> Resources.Drawable.TrappingMiscellaneous
}
