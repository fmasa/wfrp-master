package cz.frantisekmasa.wfrp_master.common.encounters

import androidx.compose.foundation.clickable
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.CharacterAvatar
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.SearchableList
import cz.frantisekmasa.wfrp_master.common.encounters.domain.Encounter
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ChooseNpcDialog(
    encounter: Encounter,
    screenModel: EncounterDetailScreenModel,
    onDismissRequest: () -> Unit,
) {
    FullScreenDialog(onDismissRequest) {
        val npcs by screenModel.allNpcsCharacters.collectWithLifecycle(null)
        var saving by remember { mutableStateOf(false) }
        val notUsedNpcs by derivedStateOf {
            if (saving) {
                return@derivedStateOf SearchableList.Data.Loading
            }

            npcs?.filter { it.id !in encounter.characters }
                ?.let { SearchableList.Data.Loaded(it) }
                ?: SearchableList.Data.Loading
        }

        val coroutineScope = rememberCoroutineScope()

        SearchableList(
            data = notUsedNpcs,
            key = { it.id },
            searchableValue = { it.name },
            navigationIcon = { CloseButton(onDismissRequest) },
            title = stringResource(Str.npcs_title_add),
            searchPlaceholder = stringResource(Str.npcs_search_placeholder),
            emptyUi = {
                EmptyUI(
                    text = stringResource(Str.npcs_messages_no_npcs),
                    icon = Resources.Drawable.Npc,
                )
            },
        ) { item ->
            ListItem(
                modifier = Modifier.clickable(
                    onClick = {
                        saving = false
                        coroutineScope.launch(Dispatchers.IO) {
                            screenModel.updateEncounter(
                                encounter.withCharacterCount(item.id, 1)
                            )
                            onDismissRequest()
                        }
                    }
                ),
                icon = {
                    CharacterAvatar(
                        item.avatarUrl,
                        fallback = Resources.Drawable.Npc,
                        size = ItemIcon.Size.Small,
                    )
                },
                text = { Text(item.name) }
            )
        }
    }
}
