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
fun ArmourDetail(
    trapping: InventoryItem,
    armour: TrappingType.Armour,
    onSaveRequest: suspend (InventoryItem) -> Unit,
) {
    Column(Modifier.verticalScroll(rememberScrollState())) {
        WornBar(trapping, armour, onSaveRequest)

        Column(Modifier.padding(Spacing.bodyPadding)) {
            val strings = LocalStrings.current

            SingleLineTextValue(
                strings.trappings.labelType,
                strings.trappings.types.armour,
            )

            EncumbranceBox(trapping)

            SingleLineTextValue(
                LocalStrings.current.armour.labelLocations,
                remember(armour.locations) {
                    armour.locations
                        .asSequence()
                        .map { it.nameResolver(strings) }
                        .sorted()
                        .joinToString(", ")
                }
            )

            SingleLineTextValue(
                strings.armour.labelArmourPoints,
                armour.points.value.toString(),
            )
            SingleLineTextValue(
                strings.trappings.labelQuantity,
                trapping.quantity.toString(),
            )

            TrappingFeatures(
                armour.qualities,
                armour.flaws
            )
            TrappingDescription(trapping)
        }
    }
}
