package cz.frantisekmasa.wfrp_master.inventory.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import cz.frantisekmasa.wfrp_master.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.core.ui.forms.*
import cz.frantisekmasa.wfrp_master.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.SaveAction
import cz.frantisekmasa.wfrp_master.inventory.domain.InventoryItem
import cz.frantisekmasa.wfrp_master.inventory.R
import cz.frantisekmasa.wfrp_master.inventory.domain.Encumbrance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.math.max

@Composable
internal fun InventoryItemDialog(
    viewModel: InventoryViewModel,
    existingItem: InventoryItem?,
    onDismissRequest: () -> Unit,
) {
    FullScreenDialog(onDismissRequest = onDismissRequest) {
        val coroutineScope = rememberCoroutineScope()
        var validate by remember { mutableStateOf(false) }

        val name = inputValue(existingItem?.name ?: "", Rules.NotBlank())

        val description = inputValue(existingItem?.description ?: "")

        val quantity = inputValue(
            existingItem?.quantity?.toString() ?: "1",
            Rules.PositiveInteger(),
        )
        val encumbrance = inputValue(
            existingItem?.encumbrance?.toString() ?: "0",
            Rules.NonNegativeNumber(),
        )

        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = { CloseButton(onClick = onDismissRequest) },
                    title = {
                        Text(
                            stringResource(
                                if (existingItem != null)
                                    R.string.title_inventory_item_edit
                                else R.string.title_inventory_item_add
                            )
                        )
                    },
                    actions = {
                        var saving by remember { mutableStateOf(false) }
                        val isValid = listOf(name, encumbrance, quantity, description)
                            .all { it.isValid() }

                        SaveAction(
                            enabled = !saving && (!validate || isValid),
                            onClick = {
                                if (!isValid) {
                                    validate = true
                                    return@SaveAction
                                }

                                saving = true

                                coroutineScope.launch(Dispatchers.IO) {
                                    viewModel.saveInventoryItem(
                                        InventoryItem(
                                            id = existingItem?.id ?: UUID.randomUUID(),
                                            name = name.value,
                                            description = description.value,
                                            quantity = max(1, quantity.toInt()),
                                            encumbrance = Encumbrance(encumbrance.toDouble()),
                                        )
                                    )

                                    withContext(Dispatchers.Main) { onDismissRequest() }
                                }
                            }
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
                TextInput(
                    label = stringResource(R.string.label_name),
                    value = name,
                    validate = validate,
                    maxLength = InventoryItem.NAME_MAX_LENGTH,
                )

                TextInput(
                    label = stringResource(R.string.inventory_item_quantity),
                    value = quantity,
                    validate = validate,
                    keyboardType = KeyboardType.Number,
                )

                TextInput(
                    label = stringResource(R.string.inventory_item_encumbrance),
                    value = encumbrance,
                    maxLength = 8,
                    validate = validate,
                    keyboardType = KeyboardType.Number,
                    filters = listOf(Filter.DigitsAndDotSymbolsOnly),
                )

                TextInput(
                    label = stringResource(R.string.label_description),
                    value = description,
                    validate = validate,
                    maxLength = InventoryItem.DESCRIPTION_MAX_LENGTH,
                )
            }
        }
    }
}