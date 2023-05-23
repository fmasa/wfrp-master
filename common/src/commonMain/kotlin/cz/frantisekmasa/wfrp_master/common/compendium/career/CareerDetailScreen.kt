package cz.frantisekmasa.wfrp_master.common.compendium.career

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.benasher44.uuid.Uuid
import com.halilibo.richtext.markdown.Markdown
import com.halilibo.richtext.ui.RichText
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Career
import cz.frantisekmasa.wfrp_master.common.core.PartyScreenModel
import cz.frantisekmasa.wfrp_master.common.core.auth.LocalUser
import cz.frantisekmasa.wfrp_master.common.core.domain.localizedName
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.shared.IO
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelable
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.DraggableListFor
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.IconAction
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.LocalPersistentSnackbarHolder
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.tabs.TabPager
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.tabs.tab
import cz.frantisekmasa.wfrp_master.common.core.ui.text.SingleLineTextValue
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CareerDetailScreen(
    private val partyId: PartyId,
    private val careerId: Uuid,
) : Screen {

    override val key = "parties/$partyId/compendium/careers/$careerId"

    @Composable
    override fun Content() {
        val screenModel: CareerCompendiumScreenModel = rememberScreenModel(arg = partyId)
        val partyScreenModel: PartyScreenModel = rememberScreenModel(arg = partyId)

        val party = partyScreenModel.party.collectWithLifecycle(null).value

        val career = remember { screenModel.get(careerId) }
            .collectWithLifecycle(null)
            .value

        if (career == null || party == null) {
            FullScreenProgress()
            return
        }

        val careerValue = career.orNull()
            ?.takeIf { it.isVisibleToPlayers || party.gameMasterId == LocalUser.current.id }

        val snackbarHolder = LocalPersistentSnackbarHolder.current
        val strings = LocalStrings.current
        val navigator = LocalNavigator.currentOrThrow

        if (careerValue == null) {
            LaunchedEffect(Unit) {
                snackbarHolder.showSnackbar(strings.careers.messages.notFound)
                navigator.pop()
            }

            return
        }

        Detail(careerValue, party, screenModel)
    }

    private sealed class LevelDialogState : Parcelable {
        @Parcelize
        object Closed : LevelDialogState()

        @Parcelize
        data class EditLevel(val level: Career.Level) : LevelDialogState()

        @Parcelize
        object AddLevel : LevelDialogState()
    }

    @Composable
    private fun Detail(career: Career, party: Party, screenModel: CareerCompendiumScreenModel) {
        val strings = LocalStrings.current.careers
        val (dialogState, setDialogState) = remember {
            mutableStateOf<LevelDialogState>(
                LevelDialogState.Closed
            )
        }

        val isGameMaster = LocalUser.current.id == party.gameMasterId
        val levelNames = remember(career) { career.levels.map { it.name }.toSet() }

        when (dialogState) {
            LevelDialogState.Closed -> {}
            LevelDialogState.AddLevel -> {
                CareerLevelDialog(
                    title = strings.titleAddLevel,
                    existingLevel = null,
                    onSave = { screenModel.saveLevel(career.id, it) },
                    onDismissRequest = { setDialogState(LevelDialogState.Closed) },
                    existingLevelNames = levelNames,
                )
            }
            is LevelDialogState.EditLevel -> {
                CareerLevelDialog(
                    title = strings.titleEditLevel,
                    existingLevel = dialogState.level,
                    onSave = { screenModel.saveLevel(career.id, it) },
                    onDismissRequest = { setDialogState(LevelDialogState.Closed) },
                    existingLevelNames = levelNames - dialogState.level.name,
                )
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = { BackButton() },
                    title = { Text(career.name) },
                    actions = {
                        var editDialogOpened by remember { mutableStateOf(false) }

                        if (editDialogOpened) {
                            CareerFormDialog(
                                title = LocalStrings.current.careers.titleEditCareer,
                                existingCareer = CareerData(
                                    name = career.name,
                                    description = career.description,
                                    races = career.races,
                                    socialClass = career.socialClass,
                                ),
                                onSave = {
                                    screenModel.update(
                                        career.copy(
                                            name = it.name,
                                            description = it.description,
                                            socialClass = it.socialClass,
                                            races = it.races,
                                        )
                                    )
                                },
                                onDismissRequest = { editDialogOpened = false },
                            )
                        }

                        if (isGameMaster) {
                            IconAction(
                                Icons.Rounded.Edit,
                                LocalStrings.current.careers.titleEditCareer,
                                onClick = { editDialogOpened = true }
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                if (isGameMaster) {
                    FloatingActionButton(onClick = { setDialogState(LevelDialogState.AddLevel) }) {
                        Icon(Icons.Rounded.Add, strings.titleAddLevel)
                    }
                }
            }
        ) {
            TabPager(fullWidthTabs = true) {
                tab(strings.tabDetail) {
                    LazyColumn(contentPadding = PaddingValues(Spacing.bodyPadding)) {
                        item {
                            SingleLineTextValue(
                                strings.labelSocialClass,
                                career.socialClass.localizedName,
                            )
                        }

                        item {
                            val globalStrings = LocalStrings.current
                            SingleLineTextValue(
                                strings.labelRaces,
                                career.races.joinToString(", ") {
                                    it.nameResolver(globalStrings)
                                },
                            )
                        }

                        item {
                            RichText {
                                Markdown(career.description)
                            }
                        }
                    }
                }

                tab(strings.tabLevels) {
                    val coroutineScope = rememberCoroutineScope()

                    LevelList(
                        career.levels,
                        onReorder = {
                            coroutineScope.launch(Dispatchers.IO) {
                                screenModel.update(career.copy(levels = it))
                            }
                        },
                        onClick = {
                            setDialogState(LevelDialogState.EditLevel(it))
                        },
                        isGameMaster = isGameMaster,
                    )
                }
            }
        }
    }
}

@Composable
private fun LevelList(
    levels: List<Career.Level>,
    onClick: (Career.Level) -> Unit,
    onReorder: (List<Career.Level>) -> Unit,
    isGameMaster: Boolean,
) {
    if (levels.isEmpty()) {
        EmptyUI(
            icon = Resources.Drawable.Career,
            text = LocalStrings.current.careers.messages.noLevel,
            subText = LocalStrings.current.careers.messages.noLevelSubtext,
        )
        return
    }

    Box(Modifier.verticalScroll(rememberScrollState())) {
        DraggableListFor(
            items = levels,
            onReorder = if (isGameMaster) onReorder else ({}),
        ) { levelIndex, level, isDragged ->
            val modifier = Modifier.fillMaxWidth()

            Card(
                elevation = if (isDragged) 6.dp else 2.dp,
                modifier = if (isGameMaster)
                    modifier.clickable { onClick(level) }
                else modifier
            ) {
                ListItem(
                    modifier = Modifier.padding(Spacing.medium),
                    text = {
                        Text(
                            buildAnnotatedString {
                                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append((levelIndex + 1).toString())
                                    append(". ")
                                    append(level.name)
                                }

                                append(" — ")
                                append(level.status.tier.localizedName)
                                append(' ')
                                append(level.status.standing.toString())
                            }
                        )
                    },
                    secondaryText = {
                        Column {
                            Text(
                                buildAnnotatedString {
                                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append(LocalStrings.current.skills.titleSkills)
                                        append(": ")
                                    }

                                    if (level.skills.isEmpty()) {
                                        append("—")
                                    }

                                    derivedStateOf { level.skills.sortedBy { it.expression.lowercase() } }.value
                                        .forEachIndexed { index, skill ->
                                            if (skill.isIncomeSkill) {
                                                withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                                                    append(skill.expression)
                                                }
                                            } else {
                                                append(skill.expression)
                                            }

                                            if (index != level.skills.lastIndex) {
                                                append(", ")
                                            }
                                        }
                                }
                            )
                            Text(
                                buildAnnotatedString {
                                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append(LocalStrings.current.talents.titleTalents)
                                        append(": ")
                                    }

                                    if (level.talents.isEmpty()) {
                                        append("—")
                                    }

                                    derivedStateOf { level.talents.sortedBy { it.lowercase() } }.value
                                        .forEachIndexed { index, talent ->
                                            append(talent)

                                            if (index != level.talents.lastIndex) {
                                                append(", ")
                                            }
                                        }
                                }
                            )

                            Text(
                                buildAnnotatedString {
                                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append(LocalStrings.current.trappings.title)
                                        append(": ")
                                    }

                                    if (level.trappings.isEmpty()) {
                                        append("—")
                                    }

                                    level.trappings.forEachIndexed { index, talent ->
                                        append(talent)

                                        if (index != level.trappings.lastIndex) {
                                            append(", ")
                                        }
                                    }
                                }
                            )
                        }
                    }
                )
            }
        }
    }
}
