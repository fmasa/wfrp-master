package cz.frantisekmasa.wfrp_master.common.encounters

import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.characterCreation.CharacterCreationScreen
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterType
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.SearchableList
import cz.frantisekmasa.wfrp_master.common.encounters.domain.Encounter
import cz.frantisekmasa.wfrp_master.common.npcs.NpcList
import cz.frantisekmasa.wfrp_master.common.npcs.NpcsScreenModel
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ChooseNpcDialog(
    encounter: Encounter,
    screenModel: EncounterDetailScreenModel,
    npcsScreenModel: NpcsScreenModel,
    onDismissRequest: () -> Unit,
) {
    FullScreenDialog(onDismissRequest) {
        val npcs by screenModel.allNpcsCharacters.collectWithLifecycle(null)
        val navigation = LocalNavigationTransaction.current
        val coroutineScope = rememberCoroutineScope()

        var saving by remember { mutableStateOf(false) }
        val notUsedNpcs by derivedStateOf {
            if (saving) {
                return@derivedStateOf SearchableList.Data.Loading
            }

            npcs?.filter { it.id !in encounter.characters }
                ?.let { SearchableList.Data.Loaded(it) }
                ?: SearchableList.Data.Loading
        }

        NpcList(
            title = stringResource(Str.npcs_title_plural),
            navigationIcon = { CloseButton(onDismissRequest) },
            data = notUsedNpcs,
            onClick = { characterId ->
                saving = false
                coroutineScope.launch(Dispatchers.IO) {
                    screenModel.updateEncounter(
                        encounter.withCharacterCount(characterId.id, 1),
                    )
                    onDismissRequest()
                }
            },
            screenModel = npcsScreenModel,
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        navigation.navigate(
                            CharacterCreationScreen(
                                partyId = screenModel.encounterId.partyId,
                                type = CharacterType.NPC,
                                userId = null,
                                encounterId = screenModel.encounterId,
                            ),
                        )
                        onDismissRequest()
                    },
                ) {
                    Icon(Icons.Rounded.Add, stringResource(Str.npcs_button_add_npc))
                }
            },
        )
    }
}
