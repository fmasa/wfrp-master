package cz.frantisekmasa.wfrp_master.common.character.trappings.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.localizedName
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItem
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.TrappingType
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.text.SingleLineTextValue
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun ArmourDetail(
    trapping: InventoryItem,
    armour: TrappingType.Armour,
    onSaveRequest: suspend (InventoryItem) -> Unit,
) {
    Column(Modifier.verticalScroll(rememberScrollState())) {
        WornBar(trapping, armour, onSaveRequest)

        Column(Modifier.padding(Spacing.bodyPadding)) {
            SingleLineTextValue(
                stringResource(Str.trappings_label_type),
                stringResource(Str.trappings_types_armour),
            )

            EncumbranceBox(trapping)

            val locations = armour.locations.map { it.localizedName }
            SingleLineTextValue(
                stringResource(Str.armour_label_locations),
                remember(locations) { locations.sorted().joinToString(", ") },
            )

            SingleLineTextValue(
                stringResource(Str.armour_label_armour_points),
                armour.points.value.toString(),
            )
            SingleLineTextValue(
                stringResource(Str.trappings_label_quantity),
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
