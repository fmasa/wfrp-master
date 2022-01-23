package cz.frantisekmasa.wfrp_master.inventory.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.shared.drawableResource
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.CardContainer
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.CardTitle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.NumberPicker
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.VisualOnlyIconDescription
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import cz.frantisekmasa.wfrp_master.inventory.domain.Armor
import cz.frantisekmasa.wfrp_master.inventory.ui.InventoryViewModel.EquippedArmour

@Composable
internal fun ArmorCard(armor: EquippedArmour, onChange: (Armor) -> Unit) {
    val change = { mutation: Armor.() -> Armor ->
        onChange(with(armor.legacyArmour, mutation))
    }

    val strings = LocalStrings.current.armour

    CardContainer(Modifier.padding(horizontal = 8.dp)) {
        CardTitle(strings.title)
        val modifier = Modifier.fillMaxWidth()

        Row(modifier = modifier) {
            ArmorPart(
                icon = Resources.Drawable.ArmorShield,
                name = strings.locations.shield,
                base = armor.armourFromItems.shield,
                points = armor.legacyArmour.shield,
                onChange = { change { copy(shield = it) } },
                modifier = Modifier.weight(1f),
            )
            ArmorPart(
                icon = Resources.Drawable.ArmorHead,
                name = strings.locations.head,
                base = armor.armourFromItems.head,
                points = armor.legacyArmour.head,
                rollRange = 1..9,
                onChange = { change { copy(head = it) } },
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.weight(1f))
        }

        Row(modifier = modifier) {
            ArmorPart(
                icon = Resources.Drawable.ArmorArmRight,
                name = strings.locations.rightArm,
                base = armor.armourFromItems.rightArm,
                points = armor.legacyArmour.rightArm,
                rollRange = 25..44,
                onChange = { change { copy(rightArm = it) } },
                modifier = Modifier.weight(1f),
            )

            ArmorPart(
                icon = Resources.Drawable.ArmorChest,
                name = strings.locations.body,
                base = armor.armourFromItems.body,
                points = armor.legacyArmour.body,
                rollRange = 45..79,
                onChange = { change { copy(body = it) } },
                modifier = Modifier.weight(1f),
            )

            ArmorPart(
                icon = Resources.Drawable.ArmorArmLeft,
                name = strings.locations.leftArm,
                base = armor.armourFromItems.leftArm,
                points = armor.legacyArmour.leftArm,
                rollRange = 10..24,
                onChange = { change { copy(leftArm = it) } },
                modifier = Modifier.weight(1f),
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
            modifier = modifier
        ) {
            ArmorPart(
                icon = Resources.Drawable.ArmorLegRight,
                name = strings.locations.rightLeg,
                base = armor.armourFromItems.rightLeg,
                points = armor.legacyArmour.rightLeg,
                rollRange = 90..100,
                onChange = { change { copy(rightLeg = it) } },
            )

            ArmorPart(
                icon = Resources.Drawable.ArmorLegLeft,
                name = strings.locations.leftLeg,
                base = armor.armourFromItems.leftLeg,
                points = armor.legacyArmour.leftLeg,
                rollRange = 80..89,
                onChange = { change { copy(leftLeg = it) } },
            )
        }
    }
}

@Composable
private fun ArmorPart(
    icon: Resources.Drawable,
    name: String,
    points: Int,
    base: Int,
    modifier: Modifier = Modifier,
    rollRange: IntRange? = null,
    onChange: (Int) -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Icon(drawableResource(icon), VisualOnlyIconDescription)
        NumberPicker(
            value = base + points,
            onIncrement = {
                if (points < Armor.MAX_VALUE) {
                    onChange(points + 1)
                }
            },
            onDecrement = {
                if (points > 0) {
                    onChange(points - 1)
                }
            },
        )

        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(name)

            if (rollRange != null) {
                Text("${formatRoll(rollRange.first)} - ${formatRoll(rollRange.last)}")
            }
        }
    }
}

private fun formatRoll(roll: Int): String = (roll % 100).toString().padStart(2, '0')
