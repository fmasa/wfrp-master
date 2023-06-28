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
import cz.frantisekmasa.wfrp_master.common.character.CharacterScreenModel
import cz.frantisekmasa.wfrp_master.common.core.domain.Characteristic
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterTab
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.HorizontalLine
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.LocalPersistentSnackbarHolder
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.Subtitle
import cz.frantisekmasa.wfrp_master.common.core.ui.settings.SettingsCard
import cz.frantisekmasa.wfrp_master.common.core.ui.settings.SettingsTitle
import cz.frantisekmasa.wfrp_master.common.gameMaster.GameMasterScreen
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import cz.frantisekmasa.wfrp_master.common.partyList.PartyListScreen

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
            topBar = { TopBar(character.name) }
        ) {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                EditableCharacterAvatar(
                    screenModel = screenModel,
                    character = character,
                    modifier = Modifier
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
                        .zIndex(1f)
                ) {
                    val strings = LocalStrings.current

                    SettingsTitle(strings.character.titleGeneralSettings)

                    ListItem(
                        text = { Text(strings.character.titleBasics) },
                        secondaryText = { Text(strings.character.secondaryTextBasics) },
                        modifier = Modifier.clickable { openSection(Section.BASICS) },
                    )

                    ListItem(
                        text = { Text(strings.character.titleCareer) },
                        secondaryText = { Text(strings.character.secondaryTextCareer) },
                        modifier = Modifier.clickable { openSection(Section.CAREER) },
                    )

                    ListItem(
                        text = { Text(strings.character.titleCharacteristics) },
                        modifier = Modifier.clickable { openSection(Section.CHARACTERISTICS) },
                        secondaryText = {
                            Text(
                                remember {
                                    Characteristic.values().joinToString { it.getShortcutName() }
                                }
                            )
                        }
                    )

                    ListItem(
                        text = { Text(strings.points.wounds) },
                        secondaryText = { Text(strings.character.secondaryTextWounds) },
                        modifier = Modifier.clickable { openSection(Section.WOUNDS) },
                    )

                    ListItem(
                        text = { Text(strings.points.experience) },
                        secondaryText = { Text(strings.character.secondaryTextExperience) },
                        modifier = Modifier.clickable { openSection(Section.EXPERIENCE) },
                    )

                    ListItem(
                        text = { Text(strings.character.titleWellBeing) },
                        secondaryText = { Text(strings.character.secondaryTextWellBeing) },
                        modifier = Modifier.clickable { openSection(Section.WELL_BEING) },
                    )

                    SettingsTitle(strings.character.titleUiSettings)

                    ListItem(
                        text = { Text(strings.character.titleVisibleTabs) },
                        secondaryText = {
                            val tabs = remember { CharacterTab.values().toSet() }

                            Text(
                                strings.character.tabsVisible(
                                    tabs.size - character.hiddenTabs.size,
                                    tabs.size,
                                )
                            )
                        },
                        modifier = Modifier.clickable { openSection(Section.VISIBLE_TABS) },
                    )

                    HorizontalLine()

                    val snackbarHolder = LocalPersistentSnackbarHolder.current
                    var removalDialogOpened by remember { mutableStateOf(false) }

                    if (removalDialogOpened) {
                        CharacterRemovalDialog(
                            character = character,
                            onDismissRequest = { removalDialogOpened = false },
                            onConfirmation = {
                                screenModel.archive()
                                snackbarHolder.showSnackbar(
                                    strings.character.messages.characterRemoved,
                                )
                                navigation.goBackTo {
                                    it is GameMasterScreen || it == PartyListScreen
                                }
                            },
                        )
                    }

                    ListItem(
                        text = { Text(strings.character.titleRemoval) },
                        secondaryText = { Text(strings.character.secondaryTextRemoval) },
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
                    Subtitle(LocalStrings.current.character.titleEdit)
                }
            },
        )
    }
}
