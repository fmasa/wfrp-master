package cz.frantisekmasa.wfrp_master.common.npcs

import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.CharacterDetailScreen
import cz.frantisekmasa.wfrp_master.common.characterCreation.CharacterCreationScreen
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterType
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.HamburgerButton
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.SearchableList
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import dev.icerock.moko.resources.compose.stringResource

class NpcsScreen(
    private val partyId: PartyId,
) : Screen {
    @Composable
    override fun Content() {
        val navigation = LocalNavigationTransaction.current
        val screenModel: NpcsScreenModel = rememberScreenModel(arg = partyId)

        val npcs by screenModel.npcs.collectWithLifecycle(null)
        val data by derivedStateOf {
            npcs?.let { SearchableList.Data.Loaded(it) }
                ?: SearchableList.Data.Loading
        }

        NpcList(
            title = stringResource(Str.npcs_title_plural),
            navigationIcon = { HamburgerButton() },
            data = data,
            onClick = { navigation.navigate(CharacterDetailScreen(it)) },
            screenModel = screenModel,
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        navigation.navigate(
                            CharacterCreationScreen(partyId, CharacterType.NPC, null),
                        )
                    },
                ) {
                    Icon(Icons.Rounded.Add, stringResource(Str.npcs_button_add_npc))
                }
            },
        )
    }
}
