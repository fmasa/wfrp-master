package cz.frantisekmasa.wfrp_master.common.character.trappings.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Encumbrance
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItem
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.text.SingleLineTextValue
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun SimpleTrappingDetailBody(
    subheadBar: @Composable ColumnScope.() -> Unit = {},
    trappingType: String,
    encumbrance: Encumbrance,
    description: String,
    characterTrapping: InventoryItem? = null,
) {
    Column(Modifier.verticalScroll(rememberScrollState())) {
        subheadBar()

        Column(Modifier.padding(Spacing.bodyPadding)) {
            SingleLineTextValue(stringResource(Str.trappings_label_type), trappingType)

            EncumbranceBox(encumbrance, characterTrapping)

            TrappingDescription(description)
        }
    }
}
