package cz.frantisekmasa.wfrp_master.common.character

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cz.frantisekmasa.wfrp_master.common.character.characteristics.CharacteristicsScreen
import cz.frantisekmasa.wfrp_master.common.character.conditions.ConditionsScreen
import cz.frantisekmasa.wfrp_master.common.character.religion.ReligionScreen
import cz.frantisekmasa.wfrp_master.common.character.skills.SkillsScreen
import cz.frantisekmasa.wfrp_master.common.character.spells.CharacterSpellsScreen
import cz.frantisekmasa.wfrp_master.common.character.trappings.TrappingsScreen
import cz.frantisekmasa.wfrp_master.common.characterCreation.CharacterCreationScreen
import cz.frantisekmasa.wfrp_master.common.characterEdit.CharacterEditScreen
import cz.frantisekmasa.wfrp_master.common.combat.ActiveCombatBanner
import cz.frantisekmasa.wfrp_master.common.core.LocalStaticConfiguration
import cz.frantisekmasa.wfrp_master.common.core.PartyScreenModel
import cz.frantisekmasa.wfrp_master.common.core.auth.LocalUser
import cz.frantisekmasa.wfrp_master.common.core.auth.UserId
import cz.frantisekmasa.wfrp_master.common.core.config.Platform
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.common.core.shared.IO
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.HamburgerButton
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.Breadcrumbs
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.IconAction
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.Subtitle
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.tabs.TabPager
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.tabs.tab
import cz.frantisekmasa.wfrp_master.common.gameMaster.GameMasterScreen
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import cz.frantisekmasa.wfrp_master.common.partyList.PartyListScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class CharacterDetailScreen(
    private val characterId: CharacterId,
    private val comingFromCombat: Boolean = false,
) : Screen {
    @Composable
    override fun Content() {
        val screenModel: CharacterScreenModel = rememberScreenModel(arg = characterId)
        val partyScreenModel: PartyScreenModel = rememberScreenModel(arg = characterId.partyId)

        val character = screenModel.character.collectWithLifecycle(null).value
        val party = partyScreenModel.party.collectWithLifecycle(null).value

        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(characterId) {
            withContext(Dispatchers.IO) {
                if (!screenModel.characterExists()) {
                    navigator.replace(
                        CharacterCreationScreen(
                            characterId.partyId,
                            // TODO: Improve type-safety
                            UserId(characterId.id)
                        )
                    )
                }
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = { HamburgerButton() },
                    title = {
                        Column {
                            character?.let { Text(it.name) }
                            party?.let { Subtitle(it.name) }
                        }
                    },
                    actions = {
                        IconAction(
                            Icons.Rounded.Edit,
                            LocalStrings.current.character.titleEdit,
                            onClick = { navigator.push(CharacterEditScreen(characterId)) },
                        )
                    }
                )
            }
        ) {
            MainContainer(
                character = character,
                party = party,
                screenModel = screenModel,
            )
        }
    }

    @Composable
    private fun MainContainer(
        character: Character?,
        party: Party?,
        screenModel: CharacterScreenModel,
    ) {
        if (character == null || party == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

            return
        }

        Column(Modifier.fillMaxSize()) {
            if (LocalStaticConfiguration.current.platform == Platform.Desktop) {
                val strings = LocalStrings.current
                val userId = LocalUser.current.id

                Breadcrumbs {
                    level(strings.parties.titleParties) { PartyListScreen }

                    if (party.gameMasterId == userId) {
                        level(party.name) { GameMasterScreen(party.id) }
                    }

                    level(character.name)
                }
            }

            if (!comingFromCombat) {
                // Prevent long and confusing back stack when user goes i.e.
                // combat -> character detail -> combat
                ActiveCombatBanner(party)
            }

            val strings = LocalStrings.current.character

            TabPager(Modifier.weight(1f)) {
                val modifier = Modifier.width(screenWidth)

                tab(strings.tabAttributes) {
                    CharacteristicsScreen(
                        characterId = characterId,
                        character = character,
                        party = party,
                        modifier = modifier,
                        screenModel = rememberScreenModel(arg = characterId),
                    )
                }

                tab(strings.tabConditions) {
                    ConditionsScreen(
                        character = character,
                        screenModel = screenModel,
                        modifier = modifier,
                    )
                }

                tab(strings.tabSkills) {
                    SkillsScreen(
                        screenModel = screenModel,
                        skillsScreenModel = rememberScreenModel(arg = characterId),
                        talentsScreenModel = rememberScreenModel(arg = characterId),
                        modifier = modifier,
                    )
                }

                tab(strings.tabSpells) {
                    CharacterSpellsScreen(
                        screenModel = rememberScreenModel(arg = characterId),
                        modifier = modifier,
                    )
                }

                tab(strings.tabReligions) {
                    ReligionScreen(
                        modifier = modifier,
                        character = character,
                        updateCharacter = screenModel::update,
                        blessingsScreenModel = rememberScreenModel(arg = characterId),
                        miraclesScreenModel = rememberScreenModel(arg = characterId)
                    )
                }

                tab(strings.tabTrappings) {
                    TrappingsScreen(
                        characterId = characterId,
                        screenModel = rememberScreenModel(arg = characterId),
                        modifier = modifier,
                    )
                }
            }
        }
    }
}
