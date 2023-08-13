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
fun AmmunitionDetail(
    trapping: InventoryItem,
    ammunition: TrappingType.Ammunition,
    onSaveRequest: suspend (InventoryItem) -> Unit,
) {
    Column(Modifier.verticalScroll(rememberScrollState())) {
        QuantityBar(trapping, onSaveRequest)

        Column(Modifier.padding(Spacing.bodyPadding)) {
            SingleLineTextValue(
                stringResource(Str.trappings_label_type),
                stringResource(Str.trappings_types_ammunition),
            )

            EncumbranceBox(trapping)

            SingleLineTextValue(
                stringResource(Str.weapons_label_damage),
                ammunition.damage.value,
            )

            SingleLineTextValue(
                stringResource(Str.weapons_label_range),
                ammunition.range.value,
            )

            val weaponGroups = ammunition.weaponGroups.map { it.localizedName }
            SingleLineTextValue(
                stringResource(Str.weapons_label_groups),
                remember(weaponGroups) {
                    weaponGroups.sorted().joinToString(", ")
                },
            )

            TrappingFeatures(ammunition.qualities, ammunition.flaws)

            TrappingDescription(trapping)
        }
    }
}
