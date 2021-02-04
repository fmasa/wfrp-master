package cz.muni.fi.rpg.ui.gameMaster.encounters

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
import cz.frantisekmasa.wfrp_master.combat.domain.encounter.Encounter
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.core.ui.forms.inputValue
import cz.frantisekmasa.wfrp_master.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.SaveAction
import cz.frantisekmasa.wfrp_master.core.viewModel.viewModel
import cz.muni.fi.rpg.viewModels.EncountersViewModel
import cz.muni.fi.rpg.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.parameter.parametersOf
import java.util.*

@Composable
fun EncounterDialog(
    existingEncounter: Encounter?,
    partyId: PartyId,
    onDismissRequest: () -> Unit,
) {
    val viewModel: EncountersViewModel by viewModel { parametersOf(partyId) }
    var validate by remember { mutableStateOf(false) }

    val name = inputValue(existingEncounter?.name ?: "", Rules.NotBlank())
    val description = inputValue(existingEncounter?.description ?: "")

    FullScreenDialog(onDismissRequest = onDismissRequest) {
        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = { CloseButton(onClick = onDismissRequest) },
                    title = {
                        Text(
                            stringResource(
                                when {
                                    existingEncounter != null -> R.string.title_encounter_edit
                                    else -> R.string.title_encounter_create
                                }
                            )
                        )
                    },
                    actions = {
                        val coroutineScope = rememberCoroutineScope()
                        var saving by remember { mutableStateOf(false) }

                        SaveAction(
                            enabled = !saving,
                            onClick = {
                                if (!name.isValid() || !description.isValid()) {
                                    validate = true
                                    return@SaveAction
                                }

                                saving = true

                                coroutineScope.launch(Dispatchers.IO) {
                                    val encounterId = existingEncounter?.id

                                    if (encounterId == null) {
                                        viewModel.createEncounter(name.value, description.value)
                                    } else {
                                        viewModel.updateEncounter(
                                            encounterId,
                                            name.value,
                                            description.value,
                                        )
                                    }

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
                    maxLength = Encounter.NAME_MAX_LENGTH,
                )

                TextInput(
                    label = stringResource(R.string.label_description),
                    value = description,
                    validate = validate,
                    maxLength = Encounter.DESCRIPTION_MAX_LENGTH,
                    multiLine = true,
                )
            }
        }
    }
}