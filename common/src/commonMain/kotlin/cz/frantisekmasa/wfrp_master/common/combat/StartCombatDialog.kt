package cz.frantisekmasa.wfrp_master.common.combat

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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.shared.IO
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardContainer
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardTitle
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.NumberPicker
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.TopBarAction
import cz.frantisekmasa.wfrp_master.common.encounters.domain.Encounter
import cz.frantisekmasa.wfrp_master.common.encounters.domain.Npc
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun StartCombatDialog(
    encounter: Encounter,
    onDismissRequest: () -> Unit,
    onComplete: () -> Unit,
    screenModel: CombatScreenModel,
) {
    FullScreenDialog(onDismissRequest = onDismissRequest) {
        val npcs: MutableMap<Npc, Boolean> = remember { mutableStateMapOf() }
        val characters: MutableMap<Character, Boolean> = remember { mutableStateMapOf() }
        val npcCharacters: MutableMap<Character, Int> = remember { mutableStateMapOf() }

        LaunchedEffect(encounter.id) {
            withContext(Dispatchers.IO) {
                val npcsAsync = async { screenModel.loadNpcsFromEncounter(encounter.id) }
                val charactersAsync = async { screenModel.loadCharacters() }
                val npcCharactersAsync = async {
                    screenModel.loadNpcs()
                        .map { it to (encounter.characters[it.id] ?: 0) }
                        .filter { (_, count) -> count > 0 }
                }

                npcs.putAll(npcsAsync.await().associateWith { true })
                characters.putAll(charactersAsync.await().associateWith { true })
                npcCharacters.putAll(npcCharactersAsync.await())
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = { CloseButton(onClick = onDismissRequest) },
                    title = { Text(LocalStrings.current.combat.titleStartCombat) },
                    actions = {
                        val saving by remember { mutableStateOf(false) }
                        val coroutineScope = rememberCoroutineScope()

                        TopBarAction(
                            text = LocalStrings.current.commonUi.buttonSave,
                            onClick = {
                                coroutineScope.launch(Dispatchers.IO) {
                                    screenModel.startCombat(
                                        encounter.id,
                                        pickCheckedOnes(characters),
                                        pickCheckedOnes(npcs),
                                        npcCharacters.filterValues { it > 0 },
                                    )
                                    onComplete()
                                }
                            },
                            enabled = !saving &&
                                (isAtLeastOneChecked(npcs) || npcCharacters.any { it.value > 0 }) &&
                                isAtLeastOneChecked(characters),
                        )
                    }
                )
            }
        ) {
            Column(
                // TODO: Consider LazyColumn
                Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(Spacing.bodyPadding),
                verticalArrangement = Arrangement.spacedBy(Spacing.small),
            ) {
                CombatantList(
                    title = LocalStrings.current.combat.titleCharacterCombatants,
                    items = characters,
                    nameFactory = { it.name },
                )

                if (npcCharacters.isNotEmpty()) {
                    NpcCharacterList(npcCharacters)
                } else {
                    CombatantList(
                        title = LocalStrings.current.combat.titleNpcCombatants,
                        items = npcs,
                        nameFactory = { it.name },
                    )
                }
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
    title: String,
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

@Composable
private fun NpcCharacterList(items: MutableMap<Character, Int>) {
    CardContainer(Modifier.fillMaxWidth()) {
        CardTitle(LocalStrings.current.combat.titleNpcCombatants)

        val sortedCharacters by derivedStateOf {
            items.toList().sortedBy { (character, _) -> character.name }
        }

        for ((character, count) in sortedCharacters) {
            ListItem(
                text = { Text(character.name) },
                trailing = {
                    NumberPicker(
                        value = count,
                        onIncrement = { items[character] = count + 1 },
                        onDecrement = { items[character] = (count + 1).coerceAtLeast(0) },
                    )
                },
            )
        }
    }
}
