package cz.muni.fi.rpg.ui.gameMaster.encounters

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.res.stringResource
import cz.frantisekmasa.wfrp_master.combat.domain.encounter.Encounter
import cz.frantisekmasa.wfrp_master.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.core.ui.forms.TextInput
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
    partyId: UUID,
    onDismissRequest: () -> Unit,
) {
    val viewModel: EncountersViewModel by viewModel { parametersOf(partyId) }
    var validate by remember { mutableStateOf(false) }

    var name by savedInstanceState { existingEncounter?.name ?: "" }
    var description by savedInstanceState { existingEncounter?.description ?: "" }

    FullScreenDialog(onDismissRequest = onDismissRequest) {
        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = { CloseButton(onClick = onDismissRequest) },
                    title = {},
                    actions = {
                        val coroutineScope = rememberCoroutineScope()
                        var saving by remember { mutableStateOf(false) }

                        SaveAction(
                            enabled = !saving,
                            onClick = {
                                if (name.isBlank()) {
                                    validate = true
                                    return@SaveAction
                                }

                                saving = true

                                coroutineScope.launch(Dispatchers.IO) {
                                    val encounterId = existingEncounter?.id

                                    if (encounterId == null) {
                                        viewModel.createEncounter(name, description)
                                    } else {
                                        viewModel.updateEncounter(encounterId, name, description)
                                    }

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
                    maxLength = Encounter.NAME_MAX_LENGTH,
                    rules = Rules(Rules.NotBlank()),
                )

                TextInput(
                    label = stringResource(R.string.label_description),
                    value = description,
                    onValueChange = { description = it },
                    validate = validate,
                    maxLength = Encounter.DESCRIPTION_MAX_LENGTH,
                    multiLine = true,
                )
            }
        }
    }
}