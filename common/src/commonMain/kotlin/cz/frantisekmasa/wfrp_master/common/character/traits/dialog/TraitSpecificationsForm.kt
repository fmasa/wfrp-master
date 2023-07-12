package cz.frantisekmasa.wfrp_master.common.character.traits.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.character.traits.TraitsScreenModel
import cz.frantisekmasa.wfrp_master.common.compendium.domain.exceptions.CompendiumItemNotFound
import cz.frantisekmasa.wfrp_master.common.core.domain.traits.Trait
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.LocalPersistentSnackbarHolder
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.SaveAction
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
internal fun TraitSpecificationsForm(
    defaultSpecifications: Map<String, String>,
    existingTrait: Trait?,
    compendiumTraitId: Uuid,
    screenModel: TraitsScreenModel,
    onDismissRequest: () -> Unit,
) {
    val specifications by derivedStateOf { defaultSpecifications.keys.sorted() }

    var validate by remember { mutableStateOf(false) }
    var saving by remember { mutableStateOf(false) }
    val inputValues = rememberSaveable(existingTrait?.id, specifications) {
        specifications.associateWith {
            mutableStateOf(defaultSpecifications.getValue(it))
        }
    }

    val strings = LocalStrings.current.traits
    val snackbarHolder = LocalPersistentSnackbarHolder.current

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { CloseButton(onDismissRequest) },
                title = {
                    Text(if (existingTrait != null) strings.titleEdit else strings.titleEdit)
                },
                actions = {
                    val coroutineScope = rememberCoroutineScope()

                    SaveAction(
                        enabled = !saving,
                        onClick = {
                            coroutineScope.launch(Dispatchers.IO) {
                                saving = true

                                if (inputValues.any { (_, state) -> state.value.isBlank() }) {
                                    validate = true
                                    saving = false
                                    return@launch
                                }

                                try {
                                    val specificationValues = inputValues.mapValues { it.value.value }
                                    if (existingTrait == null) {
                                        screenModel.saveNewTrait(
                                            compendiumTraitId = compendiumTraitId,
                                            specificationValues = specificationValues,
                                        )
                                    } else {
                                        screenModel.saveTrait(
                                            trait = existingTrait.copy(
                                                specificationValues = specificationValues
                                            ),
                                            existingTrait = existingTrait,
                                        )
                                    }
                                } catch (e: CompendiumItemNotFound) {
                                    Napier.d(e.toString(), e)

                                    snackbarHolder.showSnackbar(
                                        strings.messages.compendiumTraitRemoved
                                    )
                                } finally {
                                    onDismissRequest()
                                }
                            }
                        }
                    )
                }
            )
        }
    ) {
        if (saving) {
            FullScreenProgress()
            return@Scaffold
        }

        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(Spacing.bodyPadding),
        ) {
            inputValues.forEach { (specificationName, state) ->
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
}
