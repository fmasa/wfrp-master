package cz.frantisekmasa.wfrp_master.common.compendium.trait

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumItemDialog
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumItemFormData
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trait
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.inputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun TraitDialog(
    trait: Trait?,
    onDismissRequest: () -> Unit,
    onSaveRequest: suspend (Trait) -> Unit,
) {
    val formData = TraitFormData.fromTrait(trait)

    CompendiumItemDialog(
        onDismissRequest = onDismissRequest,
        title =
            stringResource(
                if (trait == null) {
                    Str.traits_title_new
                } else {
                    Str.traits_title_edit
                },
            ),
        formData = formData,
        saver = onSaveRequest,
    ) { validate ->
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(Spacing.bodyPadding),
        ) {
            TextInput(
                label = stringResource(Str.traits_label_name),
                value = formData.name,
                validate = validate,
                maxLength = Trait.NAME_MAX_LENGTH,
            )

            TextInput(
                label = stringResource(Str.traits_label_specifications),
                value = formData.specifications,
                validate = validate,
                helperText = stringResource(Str.traits_specifications_helper),
            )

            TextInput(
                label = stringResource(Str.traits_label_description),
                value = formData.description,
                validate = validate,
                maxLength = Trait.DESCRIPTION_MAX_LENGTH,
                multiLine = true,
                helperText = stringResource(Str.common_ui_markdown_supported_note),
            )
        }
    }
}

@Stable
private data class TraitFormData(
    val id: Uuid,
    val name: InputValue,
    val specifications: InputValue,
    val description: InputValue,
    val isVisibleToPlayers: Boolean,
) : CompendiumItemFormData<Trait> {
    companion object {
        @Composable
        fun fromTrait(trait: Trait?) =
            TraitFormData(
                id = remember { trait?.id ?: uuid4() },
                name = inputValue(trait?.name ?: "", Rules.NotBlank()),
                specifications = inputValue(trait?.specifications?.joinToString(",") ?: ""),
                description = inputValue(trait?.description ?: ""),
                isVisibleToPlayers = trait?.isVisibleToPlayers ?: false,
            )
    }

    override fun toValue() =
        Trait(
            id = id,
            name = name.value,
            specifications =
                if (specifications.value == "") {
                    emptySet()
                } else {
                    specifications.value
                        .split(',')
                        .asSequence()
                        .map { it.trim() }
                        .toSet()
                },
            description = description.value,
            isVisibleToPlayers = isVisibleToPlayers,
        )

    override fun isValid() = listOf(name, specifications, description).all { it.isValid() }
}
