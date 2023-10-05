package cz.frantisekmasa.wfrp_master.common.character.traits.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.traits.Trait
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.FormDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.HydratedFormData
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.LocalPersistentSnackbarHolder
import dev.icerock.moko.resources.compose.stringResource

@Composable
internal fun TraitSpecificationsForm(
    defaultSpecifications: Map<String, String>,
    existingTrait: Trait?,
    onSave: suspend (Map<String, String>) -> TraitSpecificationsForm.SavingResult,
    onDismissRequest: () -> Unit,
) {
    val specifications = remember(defaultSpecifications.keys) {
        defaultSpecifications.keys.sorted()
    }
    val formData = TraitSpecificationsForm.FormData(
        rememberSaveable(existingTrait?.id, specifications) {
            specifications.associateWith {
                mutableStateOf(defaultSpecifications.getValue(it))
            }
        }
    )

    val snackbarHolder = LocalPersistentSnackbarHolder.current
    val messageCompendiumTraitRemoved = stringResource(Str.traits_messages_compendium_trait_removed)

    FormDialog(
        title = stringResource(
            if (existingTrait != null)
                Str.traits_title_edit
            else Str.traits_title_new
        ),
        formData = formData,
        onDismissRequest = onDismissRequest,
        onSave = {
            when (onSave(formData.inputValues.mapValues { it.value.value })) {
                TraitSpecificationsForm.SavingResult.SUCCESS -> {}
                TraitSpecificationsForm.SavingResult.COMPENDIUM_ITEM_WAS_REMOVED -> {
                    snackbarHolder.showSnackbar(messageCompendiumTraitRemoved)
                }
            }
            onDismissRequest()
        }
    ) { validate ->
        formData.inputValues.forEach { (specificationName, state) ->
            key(specificationName) {
                TextInput(
                    label = specificationName,
                    value = InputValue(state, Rules(Rules.NotBlank())),
                    validate = validate,
                )
            }
        }
    }
}

object TraitSpecificationsForm {
    enum class SavingResult { SUCCESS, COMPENDIUM_ITEM_WAS_REMOVED }

    data class FormData(
        val inputValues: Map<String, MutableState<String>>
    ) : HydratedFormData<Map<String, String>> {
        override fun isValid(): Boolean = inputValues.all { it.value.value.isNotBlank() }

        override fun toValue(): Map<String, String> = inputValues.mapValues { it.value.value }
    }
}
