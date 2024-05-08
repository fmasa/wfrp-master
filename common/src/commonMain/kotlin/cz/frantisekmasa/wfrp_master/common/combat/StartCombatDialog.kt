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
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardContainer
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardTitle
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.NumberPicker
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.TopBarAction
import cz.frantisekmasa.wfrp_master.common.encounters.domain.Encounter
import dev.icerock.moko.resources.compose.stringResource
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
        val characters: MutableMap<Character, Boolean> = remember { mutableStateMapOf() }
        val npcCharacters: MutableMap<Character, Int> = remember { mutableStateMapOf() }

        LaunchedEffect(encounter.id) {
            withContext(Dispatchers.IO) {
                val charactersAsync = async { screenModel.loadCharacters() }
                val npcCharactersAsync =
                    async {
                        screenModel.loadNpcs()
                            .map { it to (encounter.characters[it.id] ?: 0) }
                            .filter { (_, count) -> count > 0 }
                    }

                characters.putAll(charactersAsync.await().associateWith { true })
                npcCharacters.putAll(npcCharactersAsync.await())
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = { CloseButton(onClick = onDismissRequest) },
                    title = { Text(stringResource(Str.combat_title_start_combat)) },
                    actions = {
                        val saving by remember { mutableStateOf(false) }
                        val coroutineScope = rememberCoroutineScope()

                        TopBarAction(
                            text = stringResource(Str.common_ui_button_save),
                            onClick = {
                                coroutineScope.launch(Dispatchers.IO) {
                                    screenModel.startCombat(
                                        encounter.id,
                                        pickCheckedOnes(characters),
                                        npcCharacters.filterValues { it > 0 },
                                    )
                                    onComplete()
                                }
                            },
                            enabled =
                                !saving &&
                                    npcCharacters.any { it.value > 0 } &&
                                    isAtLeastOneChecked(characters),
                        )
                    },
                )
            },
        ) {
            Column(
                // TODO: Consider LazyColumn
                Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(Spacing.bodyPadding),
                verticalArrangement = Arrangement.spacedBy(Spacing.small),
            ) {
                CombatantList(
                    title = stringResource(Str.combat_title_character_combatants),
                    items = characters,
                    nameFactory = { it.name },
                )

                NpcCharacterList(npcCharacters)
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
    nameFactory: (T) -> String,
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
                modifier =
                    Modifier.toggleable(
                        value = items[item] ?: false,
                        onValueChange = { items[item] = it },
                    ),
                text = { Text(nameFactory(item)) },
            )
        }
    }
}

@Composable
private fun NpcCharacterList(items: MutableMap<Character, Int>) {
    CardContainer(Modifier.fillMaxWidth()) {
        CardTitle(stringResource(Str.combat_title_npc_combatants))

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
                        onDecrement = { items[character] = (count - 1).coerceAtLeast(0) },
                    )
                },
            )
        }
    }
}
