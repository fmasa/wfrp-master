package cz.frantisekmasa.wfrp_master.common.compendium.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumItemDialog
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumItemFormData
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumScreenModel
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumTab
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trait
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.DialogState
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.inputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

@Composable
fun TraitCompendiumTab(screenModel: CompendiumScreenModel, width: Dp) {
    val strings = LocalStrings.current.traits.messages

    CompendiumTab(
        liveItems = screenModel.traits,
        emptyUI = {
            EmptyUI(
                text = strings.noTraitsInCompendium,
                subText = strings.noTraitsInCompendiumSubtext,
                icon = Resources.Drawable.Trait,
            )
        },
        remover = screenModel::remove,
        saver = screenModel::save,
        dialog = { TraitDialog(it, screenModel) },
        width = width,
    ) { trait ->
        ListItem(
            icon = { ItemIcon(Resources.Drawable.Trait) },
            text = { Text(trait.name) }
        )
        Divider()
    }
}

@Stable
private data class TraitFormData(
    val id: Uuid,
    val name: InputValue,
    val specifications: InputValue,
    val description: InputValue,
) : CompendiumItemFormData<Trait> {
    companion object {
        @Composable
        fun fromTrait(trait: Trait?) = TraitFormData(
            id = remember { trait?.id ?: uuid4() },
            name = inputValue(trait?.name ?: "", Rules.NotBlank()),
            specifications = inputValue(trait?.specifications?.joinToString(",") ?: ""),
            description = inputValue(trait?.description ?: ""),
        )
    }

    override fun toValue() = Trait(
        id = id,
        name = name.value,
        specifications = if (specifications.value == "")
            emptySet()
        else description.value
            .split(',')
            .asSequence()
            .map { it.trim() }
            .toSet(),
        description = description.value,
    )

    override fun isValid() = listOf(name, specifications, description).all { it.isValid() }
}

@Composable
private fun TraitDialog(
    dialogState: MutableState<DialogState<Trait?>>,
    screenModel: CompendiumScreenModel
) {
    val dialogStateValue = dialogState.value

    if (dialogStateValue !is DialogState.Opened) {
        return
    }

    val item = dialogStateValue.item
    val formData = TraitFormData.fromTrait(item)

    val strings = LocalStrings.current.traits

    CompendiumItemDialog(
        onDismissRequest = { dialogState.value = DialogState.Closed() },
        title = if (item == null) strings.titleNew else strings.titleEdit,
        formData = formData,
        saver = screenModel::save,
    ) { validate ->
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(Spacing.bodyPadding),
        ) {
            TextInput(
                label = strings.labelName,
                value = formData.name,
                validate = validate,
                maxLength = Trait.NAME_MAX_LENGTH
            )

            TextInput(
                label = strings.labelSpecifications,
                value = formData.specifications,
                validate = validate,
                helperText = strings.specificationsHelper,
            )

            TextInput(
                label = strings.labelDescription,
                value = formData.description,
                validate = validate,
                maxLength = Trait.DESCRIPTION_MAX_LENGTH,
                multiLine = true,
            )
        }
    }
}
