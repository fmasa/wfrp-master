package cz.frantisekmasa.wfrp_master.common.character.trappings.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItem
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.text.SingleLineTextValue
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun SimpleTrappingDetail(
    trapping: InventoryItem,
    trappingType: String,
    onSaveRequest: suspend (InventoryItem) -> Unit,
) {
    Column(Modifier.verticalScroll(rememberScrollState())) {
        QuantityBar(trapping, onSaveRequest)

        Column(Modifier.padding(Spacing.bodyPadding)) {
            SingleLineTextValue(stringResource(Str.trappings_label_type), trappingType)

            EncumbranceBox(trapping)

            TrappingDescription(trapping)
        }
    }
}
