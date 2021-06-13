package cz.frantisekmasa.wfrp_master.inventory.ui

import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import cz.frantisekmasa.wfrp_master.core.ui.components.FormDialog
import cz.frantisekmasa.wfrp_master.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.core.ui.forms.*
import cz.frantisekmasa.wfrp_master.inventory.domain.InventoryItem
import cz.frantisekmasa.wfrp_master.inventory.R
import cz.frantisekmasa.wfrp_master.inventory.domain.Encumbrance
import cz.frantisekmasa.wfrp_master.inventory.domain.InventoryItemId
import java.util.*
import kotlin.math.max

@Composable
internal fun InventoryItemDialog(
    viewModel: InventoryViewModel,
    existingItem: InventoryItem?,
    onDismissRequest: () -> Unit,
) {
    FullScreenDialog(onDismissRequest = onDismissRequest) {
        val formData = InventoryItemFormData.fromItem(existingItem)

        FormDialog(
            title = if (existingItem != null)
                R.string.title_inventory_item_edit
            else R.string.title_inventory_item_add,
            onDismissRequest = onDismissRequest,
            formData = formData,
            onSave = viewModel::saveInventoryItem,
        ) { validate ->
            TextInput(
                label = stringResource(R.string.label_name),
                value = formData.name,
                validate = validate,
                maxLength = InventoryItem.NAME_MAX_LENGTH,
            )

            TextInput(
                label = stringResource(R.string.inventory_item_quantity),
                value = formData.quantity,
                validate = validate,
                keyboardType = KeyboardType.Number,
            )

            TextInput(
                label = stringResource(R.string.inventory_item_encumbrance),
                value = formData.encumbrance,
                maxLength = 8,
                validate = validate,
                keyboardType = KeyboardType.Number,
                filters = listOf(Filter.DigitsAndDotSymbolsOnly),
            )

            TextInput(
                label = stringResource(R.string.label_description),
                value = formData.description,
                validate = validate,
                maxLength = InventoryItem.DESCRIPTION_MAX_LENGTH,
            )
        }
    }
}

private class InventoryItemFormData(
    val id: InventoryItemId,
    val name: InputValue,
    val encumbrance: InputValue,
    val quantity: InputValue,
    val description: InputValue,
) : HydratedFormData<InventoryItem> {
    override fun isValid() =
        listOf(name, encumbrance, quantity, description).all { it.isValid() }

    override fun toValue() = InventoryItem(
        id = id,
        name = name.value,
        description = description.value,
        quantity = max(1, quantity.toInt()),
        encumbrance = Encumbrance(encumbrance.toDouble()),
    )

    companion object {
        @Composable
        fun fromItem(item: InventoryItem?) = InventoryItemFormData(
            id = remember(item) { item?.id ?: UUID.randomUUID() },
            name = inputValue(item?.name ?: "", Rules.NotBlank()),
            encumbrance = inputValue(item?.encumbrance?.toString() ?: "0"),
            quantity = inputValue(item?.quantity?.toString() ?: "1"),
            description = inputValue(item?.description ?: ""),
        )
    }
}