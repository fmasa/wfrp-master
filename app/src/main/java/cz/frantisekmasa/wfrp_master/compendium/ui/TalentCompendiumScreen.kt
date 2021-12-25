package cz.frantisekmasa.wfrp_master.compendium.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.DialogState
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.inputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.compendium.domain.Talent
import cz.muni.fi.rpg.R
import java.util.UUID

@Composable
fun TalentCompendiumTab(viewModel: CompendiumViewModel, width: Dp) {
    CompendiumTab(
        liveItems = viewModel.talents,
        emptyUI = {
            EmptyUI(
                textId = R.string.no_talents_in_compendium,
                subTextId = R.string.no_talents_in_compendium_sub_text,
                icon = Resources.Drawable.Skill,
            )
        },
        remover = viewModel::remove,
        saver = viewModel::save,
        dialog = { TalentDialog(it, viewModel) },
        width = width,
    ) { talent ->
        ListItem(
            icon = { ItemIcon(Resources.Drawable.Skill) },
            text = { Text(talent.name) }
        )
        Divider()
    }
}

private data class TalentFormData(
    val id: UUID,
    val name: InputValue,
    val maxTimesTaken: InputValue,
    val description: InputValue,
) : CompendiumItemFormData<Talent> {
    companion object {
        @Composable
        fun fromTalent(talent: Talent?) = TalentFormData(
            id = remember { talent?.id ?: UUID.randomUUID() },
            name = inputValue(talent?.name ?: "", Rules.NotBlank()),
            maxTimesTaken = inputValue(talent?.maxTimesTaken ?: ""),
            description = inputValue(talent?.description ?: ""),
        )
    }

    override fun toValue() = Talent(
        id = id,
        name = name.value,
        maxTimesTaken = maxTimesTaken.value,
        description = description.value,
    )

    override fun isValid() = listOf(name, maxTimesTaken, description).all { it.isValid() }
}

@Composable
private fun TalentDialog(
    dialogState: MutableState<DialogState<Talent?>>,
    viewModel: CompendiumViewModel
) {
    val dialogStateValue = dialogState.value

    if (dialogStateValue !is DialogState.Opened) {
        return
    }

    val item = dialogStateValue.item
    val formData = TalentFormData.fromTalent(item)

    CompendiumItemDialog(
        onDismissRequest = { dialogState.value = DialogState.Closed() },
        title = stringResource(if (item == null) R.string.title_talent_new else R.string.title_talent_edit),
        formData = formData,
        saver = viewModel::save,
    ) { validate ->
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(Spacing.bodyPadding),
        ) {
            TextInput(
                label = stringResource(R.string.label_name),
                value = formData.name,
                validate = validate,
                maxLength = Talent.NAME_MAX_LENGTH
            )

            TextInput(
                label = stringResource(R.string.label_talent_max_times_taken),
                value = formData.maxTimesTaken,
                validate = validate,
                maxLength = Talent.MAX_TIMES_TAKEN_MAX_LENGTH,
            )

            TextInput(
                label = stringResource(R.string.label_description),
                value = formData.description,
                validate = validate,
                maxLength = Talent.DESCRIPTION_MAX_LENGTH,
                multiLine = true,
            )
        }
    }
}
