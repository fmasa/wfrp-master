package cz.frantisekmasa.wfrp_master.common.character.trappings.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.localizedName
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItem
import cz.frantisekmasa.wfrp_master.common.core.ui.text.SingleLineTextValue
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun ItemQualitiesAndFlaws(
    trapping: InventoryItem,
) {
    Column {
        if (trapping.itemQualities.isNotEmpty()) {
            val itemQualities = trapping.itemQualities.map { it.localizedName }

            SingleLineTextValue(
                stringResource(Str.trappings_label_item_qualities),
                remember(itemQualities) {
                    itemQualities.sorted().joinToString(", ")
                },
            )
        }

        if (trapping.itemFlaws.isNotEmpty()) {
            val itemFlaws = trapping.itemFlaws.map { it.localizedName }

            SingleLineTextValue(
                stringResource(Str.trappings_label_item_flaws),
                remember(itemFlaws) {
                    itemFlaws.sorted().joinToString(", ")
                },
            )
        }
    }
}
