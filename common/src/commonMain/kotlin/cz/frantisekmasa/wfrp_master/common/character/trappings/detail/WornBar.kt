package cz.frantisekmasa.wfrp_master.common.character.trappings.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItem
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.TrappingType
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.SubheadBar
import cz.frantisekmasa.wfrp_master.common.core.utils.launchLogged
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.Dispatchers

@Composable
fun WornBar(
    trapping: InventoryItem,
    wearable: TrappingType.WearableTrapping,
    onChange: suspend (InventoryItem) -> Unit,
) {
    SubheadBar {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(stringResource(Str.trappings_label_worn))

            val coroutineScope = rememberCoroutineScope()
            var saving by remember { mutableStateOf(false) }

            Switch(
                wearable.worn,
                onCheckedChange = {
                    if (saving) {
                        return@Switch
                    }

                    coroutineScope.launchLogged(Dispatchers.IO) {
                        try {
                            saving = true

                            onChange(
                                trapping.copy(
                                    containerId = null,
                                    trappingType =
                                        if (wearable.worn) {
                                            wearable.takeOff()
                                        } else {
                                            wearable.takeOn()
                                        },
                                ),
                            )
                        } finally {
                            saving = false
                        }
                    }
                },
            )
        }
    }
}
