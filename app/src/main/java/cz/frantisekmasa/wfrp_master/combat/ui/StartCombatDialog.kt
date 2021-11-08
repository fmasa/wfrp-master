package cz.frantisekmasa.wfrp_master.combat.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Checkbox
import androidx.compose.material.ListItem
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cz.frantisekmasa.wfrp_master.combat.domain.encounter.Npc
import cz.frantisekmasa.wfrp_master.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.EncounterId
import cz.frantisekmasa.wfrp_master.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.core.ui.primitives.CardContainer
import cz.frantisekmasa.wfrp_master.core.ui.primitives.CardTitle
import cz.frantisekmasa.wfrp_master.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.TopBarAction
import cz.frantisekmasa.wfrp_master.core.viewModel.viewModel
import cz.muni.fi.rpg.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.parameter.parametersOf

@Composable
fun StartCombatDialog(
    encounterId: EncounterId,
    onDismissRequest: () -> Unit,
    onComplete: () -> Unit,
) {
    FullScreenDialog(onDismissRequest = onDismissRequest) {
        val viewModel: CombatViewModel by viewModel { parametersOf(encounterId.partyId) }
        val npcs: MutableMap<Npc, Boolean> = remember { mutableStateMapOf() }
        val characters: MutableMap<Character, Boolean> = remember { mutableStateMapOf() }

        LaunchedEffect(encounterId) {
            withContext(Dispatchers.IO) {
                val npcsAsync = async { viewModel.loadNpcsFromEncounter(encounterId) }
                val charactersAsync = async { viewModel.loadCharacters() }

                withContext(Dispatchers.Main) {
                    npcs.putAll(npcsAsync.await().map { it to true }.toMap())
                    characters.putAll(charactersAsync.await().map { it to true }.toMap())
                }
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = { CloseButton(onClick = onDismissRequest) },
                    title = { Text(stringResource(R.string.title_start_combat)) },
                    actions = {
                        val saving by remember { mutableStateOf(false) }
                        val coroutineScope = rememberCoroutineScope()

                        TopBarAction(
                            textRes = R.string.button_save,
                            onClick = {
                                coroutineScope.launch(Dispatchers.IO) {
                                    viewModel.startCombat(
                                        encounterId,
                                        pickCheckedOnes(characters),
                                        pickCheckedOnes(npcs),
                                    )
                                    withContext(Dispatchers.Main) { onComplete() }
                                }
                            },
                            enabled = !saving &&
                                isAtLeastOneChecked(npcs) &&
                                isAtLeastOneChecked(characters),
                        )
                    }
                )
            }
        ) {
            Column( // TODO: Consider LazyColumn
                Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(Spacing.bodyPadding),
                verticalArrangement = Arrangement.spacedBy(Spacing.small),
            ) {
                CombatantList(
                    title = R.string.title_characters,
                    items = characters,
                    nameFactory = { it.getName() },
                )

                CombatantList(
                    title = R.string.title_npcs,
                    items = npcs,
                    nameFactory = { it.name },
                )
            }
        }
    }
}

private fun isAtLeastOneChecked(items: Map<out Any, Boolean>) = items.containsValue(true)
private fun <T> pickCheckedOnes(items: Map<T, Boolean>): List<T> =
    items.filterValues { it }
        .keys
        .toList()

@Composable
private fun <T> CombatantList(
    @StringRes title: Int,
    items: MutableMap<T, Boolean>,
    nameFactory: (T) -> String
) {
    CardContainer(Modifier.fillMaxWidth()) {
        CardTitle(title)

        for (item in items.keys.sortedBy { nameFactory(it) }) {
            ListItem(
                icon = {
                    Checkbox(
                        checked = items[item] ?: false,
                        onCheckedChange = { items[item] = it },
                    )
                },
                modifier = Modifier.toggleable(
                    value = items[item] ?: false,
                    onValueChange = { items[item] = it },
                ),
                text = { Text(nameFactory(item)) }
            )
        }
    }
}
