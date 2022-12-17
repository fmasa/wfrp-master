package cz.frantisekmasa.wfrp_master.common.character.trappings.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItem
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.TrappingType
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.text.SingleLineTextValue
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

@Composable
fun AmmunitionDetail(
    trapping: InventoryItem,
    ammunition: TrappingType.Ammunition,
    onSaveRequest: suspend (InventoryItem) -> Unit,
) {
    Column(Modifier.verticalScroll(rememberScrollState())) {
        QuantityBar(trapping, onSaveRequest)

        Column(Modifier.padding(Spacing.bodyPadding)) {
            val strings = LocalStrings.current

            SingleLineTextValue(
                strings.trappings.labelType,
                strings.trappings.types.ammunition,
            )

            EncumbranceBox(trapping)

            SingleLineTextValue(strings.weapons.labelDamage, ammunition.damage.value)
            SingleLineTextValue(strings.weapons.labelRange, ammunition.range.value)
            SingleLineTextValue(
                strings.weapons.labelGroups,
                remember(ammunition.weaponGroups) {
                    ammunition.weaponGroups
                        .asSequence()
                        .map { it.nameResolver(strings) }
                        .sorted()
                        .joinToString(", ")
                },
            )

            TrappingFeatures(ammunition.qualities, ammunition.flaws)

            TrappingDescription(trapping)
        }
    }
}
