package cz.frantisekmasa.wfrp_master.common.character.trappings

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
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Armour
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.shared.drawableResource
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardContainer
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardTitle
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.NumberPicker
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.VisualOnlyIconDescription
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings


@Composable
internal fun ArmourCard(Armour: TrappingsScreenModel.EquippedArmour, onChange: (Armour) -> Unit) {
    val change = { mutation: Armour.() -> Armour ->
        onChange(with(Armour.legacyArmour, mutation))
    }

    val strings = LocalStrings.current.armour

    CardContainer(Modifier.padding(horizontal = 8.dp)) {
        CardTitle(strings.title)
        val modifier = Modifier.fillMaxWidth()

        Row(modifier = modifier) {
            ArmourPart(
                icon = Resources.Drawable.ArmorShield,
                name = strings.locations.shield,
                base = Armour.armourFromItems.shield,
                points = Armour.legacyArmour.shield,
                onChange = { change { copy(shield = it) } },
                modifier = Modifier.weight(1f),
            )
            ArmourPart(
                icon = Resources.Drawable.ArmorHead,
                name = strings.locations.head,
                base = Armour.armourFromItems.head,
                points = Armour.legacyArmour.head,
                rollRange = 1..9,
                onChange = { change { copy(head = it) } },
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.weight(1f))
        }

        Row(modifier = modifier) {
            ArmourPart(
                icon = Resources.Drawable.ArmorArmRight,
                name = strings.locations.rightArm,
                base = Armour.armourFromItems.rightArm,
                points = Armour.legacyArmour.rightArm,
                rollRange = 25..44,
                onChange = { change { copy(rightArm = it) } },
                modifier = Modifier.weight(1f),
            )

            ArmourPart(
                icon = Resources.Drawable.ArmorChest,
                name = strings.locations.body,
                base = Armour.armourFromItems.body,
                points = Armour.legacyArmour.body,
                rollRange = 45..79,
                onChange = { change { copy(body = it) } },
                modifier = Modifier.weight(1f),
            )

            ArmourPart(
                icon = Resources.Drawable.ArmorArmLeft,
                name = strings.locations.leftArm,
                base = Armour.armourFromItems.leftArm,
                points = Armour.legacyArmour.leftArm,
                rollRange = 10..24,
                onChange = { change { copy(leftArm = it) } },
                modifier = Modifier.weight(1f),
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
            modifier = modifier
        ) {
            ArmourPart(
                icon = Resources.Drawable.ArmorLegRight,
                name = strings.locations.rightLeg,
                base = Armour.armourFromItems.rightLeg,
                points = Armour.legacyArmour.rightLeg,
                rollRange = 90..100,
                onChange = { change { copy(rightLeg = it) } },
            )

            ArmourPart(
                icon = Resources.Drawable.ArmorLegLeft,
                name = strings.locations.leftLeg,
                base = Armour.armourFromItems.leftLeg,
                points = Armour.legacyArmour.leftLeg,
                rollRange = 80..89,
                onChange = { change { copy(leftLeg = it) } },
            )
        }
    }
}

@Composable
private fun ArmourPart(
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
                if (points < Armour.MAX_VALUE) {
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
