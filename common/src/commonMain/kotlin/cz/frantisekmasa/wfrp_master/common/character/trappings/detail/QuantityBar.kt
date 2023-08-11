package cz.frantisekmasa.wfrp_master.common.character.trappings.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItem
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.NumberPicker
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.SubheadBar
import cz.frantisekmasa.wfrp_master.common.core.utils.launchLogged
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.Dispatchers

@Composable
fun QuantityBar(
    trapping: InventoryItem,
    onChange: suspend (InventoryItem) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val onQuantityChange = { quantity: Int ->
        coroutineScope.launchLogged(Dispatchers.IO) {
            onChange(trapping.copy(quantity = quantity))
        }
    }

    SubheadBar {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(stringResource(Str.trappings_label_quantity))

            val quantity = trapping.quantity

            NumberPicker(
                value = quantity,
                onIncrement = { onQuantityChange(trapping.quantity + 1) },
                onDecrement = {
                    if (quantity > 1) {
                        onQuantityChange(quantity - 1)
                    }
                }
            )
        }
    }
}
