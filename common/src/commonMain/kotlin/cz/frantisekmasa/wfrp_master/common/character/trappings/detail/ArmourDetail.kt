package cz.frantisekmasa.wfrp_master.common.character.trappings.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.trappings.TrappingTypeOption
import cz.frantisekmasa.wfrp_master.common.core.domain.HitLocation
import cz.frantisekmasa.wfrp_master.common.core.domain.localizedName
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.ArmourFlaw
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.ArmourPoints
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.ArmourQuality
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Encumbrance
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItem
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.text.SingleLineTextValue
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun ArmourDetailBody(
    subheadBar: @Composable ColumnScope.() -> Unit = {},
    locations: Set<HitLocation>,
    points: ArmourPoints,
    qualities: Map<ArmourQuality, Int>,
    flaws: Map<ArmourFlaw, Int>,
    encumbrance: Encumbrance,
    description: String,
    characterTrapping: InventoryItem?,
) {
    Column(Modifier.verticalScroll(rememberScrollState())) {
        subheadBar()

        Column(Modifier.padding(Spacing.bodyPadding)) {
            SingleLineTextValue(
                stringResource(Str.trappings_label_type),
                TrappingTypeOption.ARMOUR.localizedName,
            )

            EncumbranceBox(encumbrance, characterTrapping)

            val locationNames = locations.map { it.localizedName }
            SingleLineTextValue(
                stringResource(Str.armour_label_locations),
                remember(locationNames) { locationNames.sorted().joinToString(", ") },
            )

            SingleLineTextValue(
                stringResource(Str.armour_label_armour_points),
                points.value.toString()
            )

            if (characterTrapping != null) {
                SingleLineTextValue(
                    stringResource(Str.trappings_label_quantity),
                    characterTrapping.quantity.toString(),
                )
            }

            TrappingFeatures(qualities, flaws)
            TrappingDescription(description)
        }
    }
}
