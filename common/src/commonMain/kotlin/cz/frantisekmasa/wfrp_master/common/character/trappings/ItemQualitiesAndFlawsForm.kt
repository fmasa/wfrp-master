package cz.frantisekmasa.wfrp_master.common.character.trappings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.localizedName
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.ItemFlaw
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.ItemQuality
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.CheckboxList
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputLabel
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.SaveAction
import cz.frantisekmasa.wfrp_master.common.core.utils.launchLogged
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.Dispatchers

@Composable
fun ItemQualitiesAndFlawsForm(
    itemName: String,
    itemQualities: Set<ItemQuality>,
    itemFlaws: Set<ItemFlaw>,
    onSaveRequest: suspend (Set<ItemQuality>, Set<ItemFlaw>) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val qualityValues = rememberSaveable { mutableStateOf(itemQualities) }
    val flawValues = rememberSaveable { mutableStateOf(itemFlaws) }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { CloseButton(onDismissRequest) },
                title = { Text(itemName) },
                actions = {
                    val coroutineScope = rememberCoroutineScope()
                    var saving by remember { mutableStateOf(false) }

                    SaveAction(
                        onClick = {
                            saving = true
                            coroutineScope.launchLogged(Dispatchers.IO) {
                                onSaveRequest(qualityValues.value, flawValues.value)
                            }
                        },
                        enabled = !saving,
                    )
                }
            )
        }
    ) {
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(Spacing.bodyPadding),
            verticalArrangement = Arrangement.spacedBy(Spacing.small),
        ) {
            InputLabel(stringResource(Str.trappings_label_item_qualities))

            CheckboxList(
                items = ItemQuality.values(),
                text = { it.localizedName },
                selected = qualityValues,
            )

            InputLabel(stringResource(Str.trappings_label_item_flaws))

            CheckboxList(
                items = ItemFlaw.values(),
                text = { it.localizedName },
                selected = flawValues,
            )
        }
    }
}
