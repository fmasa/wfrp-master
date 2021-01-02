package cz.muni.fi.rpg.ui.character.inventory

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import cz.frantisekmasa.wfrp_master.core.ui.buttons.CloseButton
import cz.muni.fi.rpg.R
import cz.frantisekmasa.wfrp_master.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.SaveAction
import cz.muni.fi.rpg.model.domain.inventory.InventoryItem
import cz.muni.fi.rpg.viewModels.InventoryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.math.max

@Composable
fun InventoryItemDialog(
    viewModel: InventoryViewModel,
    existingItem: InventoryItem?,
    onDismissRequest: () -> Unit,
) {
    FullScreenDialog(onDismissRequest = onDismissRequest) {
        val coroutineScope = rememberCoroutineScope()
        var validate by remember { mutableStateOf(false) }

        var name by savedInstanceState { existingItem?.name ?: "" }
        var description by savedInstanceState { existingItem?.description ?: "" }
        var quantity by savedInstanceState { existingItem?.quantity?.toString() ?: "1" }

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

                        SaveAction(
                            enabled = !saving && (!validate || name.isNotBlank()),
                            onClick = {
                                if (name.isBlank()) {
                                    validate = true
                                    return@SaveAction
                                }

                                saving = true

                                coroutineScope.launch(Dispatchers.IO) {
                                    viewModel.saveInventoryItem(
                                        InventoryItem(
                                            id = existingItem?.id ?: UUID.randomUUID(),
                                            name = name,
                                            description = description,
                                            quantity = max(1, quantity.toIntOrNull() ?: 1),
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
            ScrollableColumn(
                contentPadding = PaddingValues(Spacing.bodyPadding),
                verticalArrangement = Arrangement.spacedBy(Spacing.small),
            ) {
                TextInput(
                    label = stringResource(R.string.label_name),
                    value = name,
                    onValueChange = { name = it },
                    validate = validate,
                    maxLength = InventoryItem.NAME_MAX_LENGTH,
                    rules = Rules(Rules.NotBlank()),
                )

                TextInput(
                    label = stringResource(R.string.inventory_item_quantity),
                    value = quantity,
                    onValueChange = { quantity = it },
                    validate = validate,
                    keyboardType = KeyboardType.Number,
                )

                TextInput(
                    label = stringResource(R.string.label_description),
                    value = description,
                    onValueChange = { description = it },
                    validate = validate,
                    maxLength = InventoryItem.DESCRIPTION_MAX_LENGTH,
                )
            }
        }
    }
}