package cz.frantisekmasa.wfrp_master.common.character.trappings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.localizedName
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItem
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.ItemFlaw
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.ItemQuality
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.CheckboxList
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputLabel
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.inputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.SaveAction
import cz.frantisekmasa.wfrp_master.common.core.utils.launchLogged
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.Dispatchers

@Composable
fun TrappingFromCompendiumForm(
    itemName: String,
    data: TrappingFromCompendiumPlayerData,
    onSaveRequest: suspend (TrappingFromCompendiumPlayerData) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val quantityField = inputValue(data.quantity.toString(), Rules.PositiveInteger())
    val noteField = inputValue(data.note)
    val qualityValues = rememberSaveable { mutableStateOf(data.itemQualities) }
    val flawValues = rememberSaveable { mutableStateOf(data.itemFlaws) }
    val isEncumbranceCounted = rememberSaveable { mutableStateOf(data.isEncumbranceCounted) }
    var validate by remember { mutableStateOf(false) }

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
                            if (!quantityField.isValid()) {
                                validate = true
                                return@SaveAction
                            }

                            saving = true
                            coroutineScope.launchLogged(Dispatchers.IO) {
                                onSaveRequest(
                                    TrappingFromCompendiumPlayerData(
                                        itemQualities = qualityValues.value,
                                        itemFlaws = flawValues.value,
                                        quantity = quantityField.toInt().coerceAtLeast(1),
                                        note = noteField.value,
                                        isEncumbranceCounted = isEncumbranceCounted.value,
                                    ),
                                )
                                onDismissRequest()
                            }
                        },
                        enabled = !saving,
                    )
                },
            )
        },
    ) {
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(Spacing.bodyPadding),
            verticalArrangement = Arrangement.spacedBy(Spacing.small),
        ) {
            TextInput(
                label = stringResource(Str.trappings_label_note),
                value = noteField,
                validate = validate,
                multiLine = true,
                helperText = stringResource(Str.common_ui_markdown_supported_note),
                maxLength = InventoryItem.NOTE_MAX_LENGTH,
            )

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

            Column {
                TextInput(
                    label = stringResource(Str.trappings_label_quantity),
                    value = quantityField,
                    validate = validate,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                )

                EncumbranceCountedCheckbox(isEncumbranceCounted)
            }
        }
    }
}

data class TrappingFromCompendiumPlayerData(
    val itemQualities: Set<ItemQuality>,
    val itemFlaws: Set<ItemFlaw>,
    val quantity: Int,
    val note: String,
    val isEncumbranceCounted: Boolean,
)
