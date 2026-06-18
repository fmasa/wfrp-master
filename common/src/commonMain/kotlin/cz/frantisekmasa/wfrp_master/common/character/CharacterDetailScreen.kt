package cz.frantisekmasa.wfrp_master.common.character

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.characteristics.CharacteristicsScreen
import cz.frantisekmasa.wfrp_master.common.character.characteristics.CompendiumCareer
import cz.frantisekmasa.wfrp_master.common.character.combat.CharacterCombatScreen
import cz.frantisekmasa.wfrp_master.common.character.conditions.ConditionsScreen
import cz.frantisekmasa.wfrp_master.common.character.notes.NotesScreen
import cz.frantisekmasa.wfrp_master.common.character.religion.ReligionScreen
import cz.frantisekmasa.wfrp_master.common.character.skills.SkillsScreen
import cz.frantisekmasa.wfrp_master.common.character.spells.CharacterSpellsScreen
import cz.frantisekmasa.wfrp_master.common.character.trappings.TrappingsScreen
import cz.frantisekmasa.wfrp_master.common.character.wellBeing.WellBeingScreen
import cz.frantisekmasa.wfrp_master.common.characterCreation.CharacterCreationScreen
import cz.frantisekmasa.wfrp_master.common.characterEdit.CharacterEditScreen
import cz.frantisekmasa.wfrp_master.common.combat.ActiveCombatBanner
import cz.frantisekmasa.wfrp_master.common.compendium.journal.JournalScreen
import cz.frantisekmasa.wfrp_master.common.core.LocalStaticConfiguration
import cz.frantisekmasa.wfrp_master.common.core.auth.LocalUser
import cz.frantisekmasa.wfrp_master.common.core.auth.UserId
import cz.frantisekmasa.wfrp_master.common.core.config.Platform
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterTab
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterType
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.localizedName
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.logging.Reporting
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.shared.drawableResource
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.HamburgerButton
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.menu.DropdownMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.menu.DropdownMenuItem
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.Breadcrumbs
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.IconAction
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.Subtitle
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.tabs.TabPager
import cz.frantisekmasa.wfrp_master.common.gameMaster.GameMasterScreen
import cz.frantisekmasa.wfrp_master.common.partyList.PartyListScreen
import dev.icerock.moko.resources.compose.stringResource

data class CharacterDetailScreen(
    private val characterId: CharacterId,
    private val comingFromCombat: Boolean = false,
    private val initialTab: CharacterTab = CharacterTab.values().first(),
) : Screen {
    override val key = "parties/$characterId"

    @Composable
    override fun Content() {
        val screenModelV2: CharacterDetailScreenModel = rememberScreenModel(arg = characterId)
        val state = screenModelV2.state.collectWithLifecycle(null).value

        val navigation = LocalNavigationTransaction.current

        if (state == null) {
            SkeletonScaffold()
            return
        }

        val hiddenTabs = state.character.hiddenTabs
        val tabs = remember(hiddenTabs) { CharacterTab.values().filterNot { it in hiddenTabs } }

        var currentTab by rememberSaveable(tabs) {
            mutableStateOf(
                if (initialTab in tabs) {
                    initialTab
                } else {
                    tabs.firstOrNull()
                },
            )
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = { HamburgerButton() },
                    title = {
                        CharacterTitle(
                            partyId = state.characterId.partyId,
                            // TODO: Use separate field in top level state
                            career = state.characteristicsScreenState.compendiumCareer,
                            character = state.character,
                            currentTab = currentTab,
                            isGameMaster = state.isGameMaster,
                            state = state.characterPickerState,
                            assignCharacter = screenModelV2::assignCharacter,
                        )
                    },
                    actions = {
                        IconAction(
                            drawableResource(Resources.Drawable.JournalEntry),
                            stringResource(Str.compendium_title_journal),
                            onClick = {
                                Reporting.record { journalOpened("character_detail") }
                                navigation.navigate(JournalScreen(characterId.partyId))
                            },
                        )
                        IconAction(
                            Icons.Rounded.Edit,
                            stringResource(Str.character_title_edit),
                            onClick = { navigation.navigate(CharacterEditScreen(characterId)) },
                        )
                    },
                )
            },
        ) {
            MainContainer(
                isGameMaster = state.isGameMaster,
                screenModelV2 = screenModelV2,
                state = state,
                tabs = tabs,
                onTabChange = { currentTab = it },
            )
        }
    }

    @Composable
    private fun CharacterTitle(
        partyId: PartyId,
        state: CharacterPickerState,
        character: Character,
        career: CompendiumCareer?,
        assignCharacter: suspend (Character, UserId) -> Unit,
        currentTab: CharacterTab?,
        isGameMaster: Boolean,
    ) {
        val userId = LocalUser.current.id
        val canAddCharacters = !isGameMaster
        val navigation = LocalNavigationTransaction.current

        val subtitle = ((character.race?.let { "${it.localizedName} " } ?: "") +
                careerName(career, character)).takeIf { it.isNotBlank() }

        if (state.allCharacters.isNotEmpty() || canAddCharacters) {
            var unassignedCharactersDialogOpened by remember { mutableStateOf(false) }

            if (unassignedCharactersDialogOpened) {
                UnassignedCharacterPickerDialog(
                    partyId = partyId,
                    unassignedCharacters = state.assignableCharacters,
                    assignCharacter = assignCharacter,
                    onDismissRequest = { unassignedCharactersDialogOpened = false },
                    onAssigned = {
                        navigation.replace(
                            CharacterDetailScreen(
                                characterId = it,
                                comingFromCombat = comingFromCombat,
                                initialTab = currentTab ?: CharacterTab.values().first(),
                            ),
                        )
                        unassignedCharactersDialogOpened = false
                    },
                )
            }

            var dropdownOpened by remember { mutableStateOf(false) }

            Row(
                modifier =
                    if (state.allCharacters.size > 1 || canAddCharacters) {
                        Modifier.clickable { dropdownOpened = true }
                    } else {
                        Modifier
                    },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(character.name)
                    subtitle?.let { Subtitle(it) }
                }

                if (state.allCharacters.size > 1 || canAddCharacters) {
                    Icon(Icons.Rounded.ExpandMore, null)
                }
            }

            DropdownMenu(
                dropdownOpened,
                onDismissRequest = { dropdownOpened = false },
            ) {
                state.allCharacters.forEach { otherCharacter ->
                    key(otherCharacter.id) {
                        DropdownMenuItem(
                            onClick = {
                                if (otherCharacter.id != character.id) {
                                    navigation.replace(
                                        CharacterDetailScreen(
                                            characterId = CharacterId(partyId, otherCharacter.id),
                                            comingFromCombat = comingFromCombat,
                                            initialTab =
                                                currentTab ?: CharacterTab.values()
                                                    .first(),
                                        ),
                                    )
                                } else {
                                    dropdownOpened = false
                                }
                            },
                        ) {
                            Text(otherCharacter.name)
                        }
                    }
                }

                if (canAddCharacters) {
                    if (state.assignableCharacters.isNotEmpty()) {
                        DropdownMenuItem(
                            onClick = {
                                dropdownOpened = false
                                unassignedCharactersDialogOpened = true
                            },
                        ) {
                            Icon(Icons.Rounded.Add, null, modifier = Modifier.size(24.dp))
                            Text(stringResource(Str.character_button_link))
                        }
                    }

                    DropdownMenuItem(
                        onClick = {
                            navigation.navigate(
                                CharacterCreationScreen(
                                    partyId,
                                    CharacterType.PLAYER_CHARACTER,
                                    userId,
                                ),
                            )
                        },
                    ) {
                        Icon(Icons.Rounded.Add, null, modifier = Modifier.size(24.dp))
                        Text(stringResource(Str.character_button_add))
                    }
                }
            }
        } else {
            Column {
                Text(character.name)
                subtitle?.let { Subtitle(it) }
            }
        }
    }

    @Composable
    private fun MainContainer(
        isGameMaster: Boolean,
        screenModelV2: CharacterDetailScreenModel,
        state: CharacterDetailScreenState,
        tabs: List<CharacterTab>,
        onTabChange: (CharacterTab) -> Unit,
    ) {
        Column(Modifier.fillMaxSize()) {
            if (LocalStaticConfiguration.current.platform == Platform.Desktop) {
                val titleParties = stringResource(Str.parties_title_parties)
                Breadcrumbs {
                    level(titleParties) { PartyListScreen }

                    if (isGameMaster) {
                        level(state.partyName) { GameMasterScreen(state.characterId.partyId) }
                    }

                    level(state.character.name)
                }
            }

            if (!comingFromCombat && state.isCombatActive) {
                // Prevent long and confusing back stack when user goes i.e.
                // combat -> character detail -> combat
                ActiveCombatBanner(state.characterId.partyId)
            }

            if (tabs.isEmpty()) {
                val navigation = LocalNavigationTransaction.current

                LaunchedEffect(Unit) {
                    navigation.navigate(
                        CharacterEditScreen(characterId, CharacterEditScreen.Section.VISIBLE_TABS),
                    )
                }
                return@Column
            }

            TabPager(
                Modifier.weight(1f),
                initialPage =
                    remember(tabs) {
                        tabs.indices.firstOrNull { tabs[it] == initialTab } ?: 0
                    },
                onPageChange = { onTabChange(tabs[it]) },
            ) {
                val modifier = Modifier.fillMaxHeight()

                tabs.forEach { tab ->
                    tab(name = { tab.localizedName }) {
                        when (tab) {
                            CharacterTab.ATTRIBUTES -> {
                                CharacteristicsScreen(
                                    characterId = characterId,
                                    character = state.character,
                                    updatePoints = screenModelV2::updatePoints,
                                    state = state.characteristicsScreenState,
                                    modifier = modifier,
                                )
                            }
                            CharacterTab.COMBAT -> {
                                CharacterCombatScreen(
                                    characterId = characterId,
                                    state = state.combatScreenState,
                                    modifier = modifier,
                                )
                            }
                            CharacterTab.CONDITIONS -> {
                                ConditionsScreen(
                                    state = state.conditionsScreenState,
                                    updateConditions = screenModelV2::updateConditions,
                                    modifier = modifier,
                                )
                            }
                            CharacterTab.SKILLS_AND_TALENTS -> {
                                SkillsScreen(
                                    characterId = characterId,
                                    state = state.skillsScreenState,
                                    modifier = modifier,
                                    removeSkill = screenModelV2::removeSkill,
                                    removeTalent = screenModelV2::removeTalent,
                                    removeTrait = screenModelV2::removeTrait,
                                )
                            }
                            CharacterTab.SPELLS -> {
                                CharacterSpellsScreen(
                                    characterId = characterId,
                                    state = state.spellsScreenState,
                                    onRemove = screenModelV2::removeSpell,
                                    modifier = modifier,
                                )
                            }
                            CharacterTab.RELIGION -> {
                                ReligionScreen(
                                    characterId = characterId,
                                    modifier = modifier,
                                    character = state.character,
                                    state = state.religionScreenState,
                                    updateCharacter = screenModelV2::updateCharacter,
                                    removeBlessing = screenModelV2::removeBlessing,
                                    removeMiracle = screenModelV2::removeMiracle,
                                )
                            }
                            CharacterTab.TRAPPINGS -> {
                                TrappingsScreen(
                                    characterId = characterId,
                                    state = state.trappingsScreenState,
                                    onMoneyBalanceUpdate = screenModelV2::updateMoneyBalance,
                                    onAddToContainer = screenModelV2::addToContainer,
                                    onDuplicate = screenModelV2::duplicateTrapping,
                                    onRemove = screenModelV2::removeTrapping,
                                    modifier = modifier,
                                )
                            }
                            CharacterTab.WELL_BEING -> {
                                WellBeingScreen(
                                    characterId = characterId,
                                    state = state.wellBeingScreenState,
                                    removeDisease = screenModelV2::removeDisease,
                                    modifier = modifier,
                                    updateCharacter = screenModelV2::updateCharacter,
                                )
                            }
                            CharacterTab.NOTES -> {
                                NotesScreen(
                                    updateNote = screenModelV2::updateNote,
                                    updateMotivation = screenModelV2::updateMotivation,
                                    updateCharacterAmbitions = screenModelV2::updateCharacterAmbitions,
                                    state = state.notesScreenState,
                                    modifier = modifier,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SkeletonScaffold() {
    Scaffold(topBar = { TopAppBar { } }) {
        FullScreenProgress()
    }
}

@Stable
private fun careerName(
    career: CompendiumCareer?,
    character: Character,
): String {
    if (career == null) {
        return character.career
    }

    return career.level.name
}
