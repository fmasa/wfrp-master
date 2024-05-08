package cz.frantisekmasa.wfrp_master.common.characterEdit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ListItem
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import cafe.adriel.voyager.core.screen.Screen
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.CharacterScreenModel
import cz.frantisekmasa.wfrp_master.common.core.domain.Characteristic
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterTab
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterType
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.HorizontalLine
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.ConfirmationDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.LocalPersistentSnackbarHolder
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.Subtitle
import cz.frantisekmasa.wfrp_master.common.core.ui.settings.SettingsCard
import cz.frantisekmasa.wfrp_master.common.core.ui.settings.SettingsTitle
import cz.frantisekmasa.wfrp_master.common.gameMaster.GameMasterScreen
import cz.frantisekmasa.wfrp_master.common.partyList.PartyListScreen
import dev.icerock.moko.resources.compose.stringResource

data class CharacterEditScreen(
    private val characterId: CharacterId,
    private val section: Section? = null,
) : Screen {
    override val key = "parties/${characterId.partyId}/character/${characterId.id}/$section"

    enum class Section {
        EXPERIENCE,
        BASICS,
        CAREER,
        CHARACTERISTICS,
        VISIBLE_TABS,
        WELL_BEING,
        WOUNDS,
    }

    @Composable
    override fun Content() {
        val screenModel: CharacterScreenModel = rememberScreenModel(arg = characterId)
        val character = screenModel.character.collectWithLifecycle(null).value

        if (character == null) {
            FullScreenProgress()
            return
        }

        if (section != null) {
            when (section) {
                Section.EXPERIENCE -> ExperienceSection(character, screenModel)
                Section.BASICS -> BasicsSection(character, screenModel)
                Section.CAREER -> CareerSection(character, screenModel)
                Section.CHARACTERISTICS -> CharacteristicsSection(character, screenModel)
                Section.WELL_BEING -> WellBeingSection(character, screenModel)
                Section.WOUNDS -> MaxWoundsSection(character, screenModel)
                Section.VISIBLE_TABS -> VisibleTabsSection(character, screenModel)
            }

            return
        }

        Scaffold(
            topBar = { TopBar(character.name) },
        ) {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                EditableCharacterAvatar(
                    screenModel = screenModel,
                    character = character,
                    modifier =
                        Modifier
                            .zIndex(2f)
                            .align(Alignment.CenterHorizontally)
                            .padding(top = Spacing.large),
                )

                val navigation = LocalNavigationTransaction.current
                val openSection = { section: Section ->
                    navigation.navigate(CharacterEditScreen(characterId, section))
                }

                SettingsCard(
                    Modifier
                        .offset(y = -(ItemIcon.Size.XLarge.dimensions * 3 / 4))
                        .zIndex(1f),
                ) {
                    SettingsTitle(stringResource(Str.character_title_general_settings))

                    ListItem(
                        text = { Text(stringResource(Str.character_title_basics)) },
                        secondaryText = { Text(stringResource(Str.character_secondary_text_basics)) },
                        modifier = Modifier.clickable { openSection(Section.BASICS) },
                    )

                    ListItem(
                        text = { Text(stringResource(Str.character_title_career)) },
                        secondaryText = { Text(stringResource(Str.character_secondary_text_career)) },
                        modifier = Modifier.clickable { openSection(Section.CAREER) },
                    )

                    ListItem(
                        text = { Text(stringResource(Str.character_title_characteristics)) },
                        modifier = Modifier.clickable { openSection(Section.CHARACTERISTICS) },
                        secondaryText = {
                            @Suppress("SimplifiableCallChain") // stringResource() is composable
                            Text(
                                Characteristic.values()
                                    .map { stringResource(it.shortcut) }
                                    .joinToString(", "),
                            )
                        },
                    )

                    ListItem(
                        text = { Text(stringResource(Str.points_wounds)) },
                        secondaryText = { Text(stringResource(Str.character_secondary_text_wounds)) },
                        modifier = Modifier.clickable { openSection(Section.WOUNDS) },
                    )

                    ListItem(
                        text = { Text(stringResource(Str.points_experience)) },
                        secondaryText = { Text(stringResource(Str.character_secondary_text_experience)) },
                        modifier = Modifier.clickable { openSection(Section.EXPERIENCE) },
                    )

                    ListItem(
                        text = { Text(stringResource(Str.character_title_well_being)) },
                        secondaryText = { Text(stringResource(Str.character_secondary_text_well_being)) },
                        modifier = Modifier.clickable { openSection(Section.WELL_BEING) },
                    )

                    SettingsTitle(stringResource(Str.character_title_ui_settings))

                    ListItem(
                        text = { Text(stringResource(Str.character_title_visible_tabs)) },
                        secondaryText = {
                            val tabs = remember { CharacterTab.values().toSet() }

                            Text(
                                stringResource(
                                    Str.character_tabs_visible,
                                    tabs.size - character.hiddenTabs.size,
                                    tabs.size,
                                ),
                            )
                        },
                        modifier = Modifier.clickable { openSection(Section.VISIBLE_TABS) },
                    )

                    HorizontalLine()

                    val snackbarHolder = LocalPersistentSnackbarHolder.current
                    var removalDialogOpened by remember { mutableStateOf(false) }

                    if (removalDialogOpened) {
                        val messageCharacterRemoved =
                            stringResource(
                                Str.character_messages_character_removed,
                            )
                        CharacterRemovalDialog(
                            character = character,
                            onDismissRequest = { removalDialogOpened = false },
                            onConfirmation = {
                                screenModel.archive()
                                snackbarHolder.showSnackbar(messageCharacterRemoved)
                                navigation.goBackTo {
                                    it is GameMasterScreen || it == PartyListScreen
                                }
                            },
                        )
                    }

                    val isGameMaster = screenModel.isGameMaster.collectWithLifecycle(false).value

                    if (isGameMaster) {
                        if (character.type != CharacterType.PLAYER_CHARACTER) {
                            val (dialogOpened, setDialogOpened) = remember { mutableStateOf(false) }

                            if (dialogOpened) {
                                ConfirmationDialog(
                                    onDismissRequest = { setDialogOpened(false) },
                                    text = stringResource(Str.character_messages_turn_into_p_c_confirmation),
                                    confirmationButtonText = stringResource(Str.common_ui_button_yes),
                                    onConfirmation = { screenModel.update { it.turnIntoPlayerCharacter() } },
                                )
                            }

                            ListItem(
                                text = { Text(stringResource(Str.character_button_turn_into_p_c)) },
                                modifier = Modifier.clickable { setDialogOpened(true) },
                            )
                        }

                        if (character.type != CharacterType.NPC) {
                            val (dialogOpened, setDialogOpened) = remember { mutableStateOf(false) }

                            if (dialogOpened) {
                                ConfirmationDialog(
                                    onDismissRequest = { setDialogOpened(false) },
                                    text = stringResource(Str.character_messages_turn_into_n_p_c_confirmation),
                                    confirmationButtonText = stringResource(Str.common_ui_button_yes),
                                    onConfirmation = { screenModel.update { it.turnIntoNPC() } },
                                )
                            }

                            ListItem(
                                text = { Text(stringResource(Str.character_button_turn_into_n_p_c)) },
                                modifier = Modifier.clickable { setDialogOpened(true) },
                            )
                        }

                        if (character.userId != null) {
                            val (dialogOpened, setDialogOpened) = remember { mutableStateOf(false) }

                            if (dialogOpened) {
                                ConfirmationDialog(
                                    onDismissRequest = { setDialogOpened(false) },
                                    text = stringResource(Str.character_messages_unlink_from_player_confirmation),
                                    confirmationButtonText = stringResource(Str.common_ui_button_yes),
                                    onConfirmation = { screenModel.update { it.unlinkFromUser() } },
                                )
                            }

                            ListItem(
                                text = { Text(stringResource(Str.character_button_unlink_from_player)) },
                                secondaryText = { Text(stringResource(Str.character_button_unlink_from_player_subtext)) },
                                modifier = Modifier.clickable { setDialogOpened(true) },
                            )
                        }
                    }

                    ListItem(
                        text = { Text(stringResource(Str.character_title_removal)) },
                        secondaryText = { Text(stringResource(Str.character_secondary_text_removal)) },
                        modifier = Modifier.clickable { removalDialogOpened = true },
                    )
                }
            }
        }
    }

    @Composable
    private fun TopBar(characterName: String) {
        TopAppBar(
            navigationIcon = { BackButton() },
            title = {
                Column {
                    Text(characterName)
                    Subtitle(stringResource(Str.character_title_edit))
                }
            },
        )
    }
}
