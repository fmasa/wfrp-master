package cz.frantisekmasa.wfrp_master.common.gameMaster

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.CharacterDetailScreen
import cz.frantisekmasa.wfrp_master.common.characterCreation.CharacterCreationScreen
import cz.frantisekmasa.wfrp_master.common.combat.ActiveCombatBanner
import cz.frantisekmasa.wfrp_master.common.core.LocalStaticConfiguration
import cz.frantisekmasa.wfrp_master.common.core.config.Platform
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterType
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.HamburgerButton
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.Breadcrumbs
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.IconAction
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.tabs.TabPager
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.tabs.tab
import cz.frantisekmasa.wfrp_master.common.gameMaster.calendar.WorldScreen
import cz.frantisekmasa.wfrp_master.common.partyList.PartyListScreen
import cz.frantisekmasa.wfrp_master.common.partySettings.PartySettingsScreen
import dev.icerock.moko.resources.compose.stringResource

class GameMasterScreen(
    private val partyId: PartyId,
) : Screen {
    override val key = "party/$partyId"

    @Composable
    override fun Content() {
        val screenModel: GameMasterScreenModel = rememberScreenModel(arg = partyId)
        val party = screenModel.party.collectWithLifecycle(null).value

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { party?.let { Text(it.name) } },
                    navigationIcon = { HamburgerButton() },
                    actions = {
                        val navigation = LocalNavigationTransaction.current

                        IconAction(
                            Icons.Rounded.Settings,
                            stringResource(Str.parties_title_settings),
                            onClick = {
                                if (party == null) {
                                    return@IconAction
                                }

                                navigation.navigate(PartySettingsScreen(party.id))
                            },
                        )
                    },
                )
            },
        ) {
            if (party == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }

                return@Scaffold
            }

            Column(Modifier.fillMaxSize()) {
                if (LocalStaticConfiguration.current.platform == Platform.Desktop) {
                    val titleParties = stringResource(Str.parties_title_parties)
                    Breadcrumbs {
                        level(titleParties) { PartyListScreen }
                        level(party.name)
                    }
                }

                if (party.activeCombat != null) {
                    ActiveCombatBanner(party.id)
                }

                TabPager(
                    modifier = Modifier.weight(1f),
                    beyondBoundsPageCount = 1,
                    fullWidthTabs = true,
                ) {
                    val modifier = Modifier.fillMaxSize()

                    tab({ stringResource(Str.parties_tab_characters) }) {
                        val navigation = LocalNavigationTransaction.current

                        PartySummaryScreen(
                            modifier = modifier,
                            partyId = party.id,
                            screenModel = screenModel,
                            onCharacterOpenRequest = {
                                navigation.navigate(
                                    CharacterDetailScreen(CharacterId(party.id, it.id)),
                                )
                            },
                            onCharacterCreateRequest = {
                                navigation.navigate(
                                    CharacterCreationScreen(
                                        partyId,
                                        CharacterType.PLAYER_CHARACTER,
                                        it,
                                    ),
                                )
                            },
                        )
                    }

                    tab({ stringResource(Str.parties_tab_world) }) {
                        WorldScreen(
                            party,
                            modifier = modifier,
                            screenModel = screenModel,
                        )
                    }
                }
            }
        }
    }
}
