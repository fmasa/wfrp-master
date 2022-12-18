package cz.frantisekmasa.wfrp_master.common.character.trappings.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItem
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.TrappingType
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.text.SingleLineTextValue
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

@Composable
fun ClothingOrAccessoryDetail(
    trapping: InventoryItem,
    clothingOrAccessory: TrappingType.ClothingOrAccessory,
    onSaveRequest: suspend (InventoryItem) -> Unit,
) {
    Column(Modifier.verticalScroll(rememberScrollState())) {
        WornBar(trapping, clothingOrAccessory, onSaveRequest)

        Column(Modifier.padding(Spacing.bodyPadding)) {
            val strings = LocalStrings.current

            SingleLineTextValue(
                strings.trappings.labelType,
                strings.trappings.types.clothingOrAccessory,
            )

            EncumbranceBox(trapping)

            TrappingDescription(trapping)
        }
    }
}
