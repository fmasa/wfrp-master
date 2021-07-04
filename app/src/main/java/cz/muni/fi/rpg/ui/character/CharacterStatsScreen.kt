package cz.muni.fi.rpg.ui.character

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cz.frantisekmasa.wfrp_master.core.domain.Characteristic
import cz.muni.fi.rpg.R
import cz.frantisekmasa.wfrp_master.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.core.domain.character.Points
import cz.frantisekmasa.wfrp_master.core.domain.Stats
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.core.domain.Expression
import cz.frantisekmasa.wfrp_master.core.domain.character.Points.PointPool
import cz.frantisekmasa.wfrp_master.core.domain.character.SocialStatus
import cz.frantisekmasa.wfrp_master.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.core.media.rememberSoundPlayer
import cz.frantisekmasa.wfrp_master.core.ui.components.Breakpoint
import cz.frantisekmasa.wfrp_master.core.ui.components.ColumnSize.*
import cz.frantisekmasa.wfrp_master.core.ui.components.Container
import cz.frantisekmasa.wfrp_master.core.ui.components.LocalBreakpoint
import cz.frantisekmasa.wfrp_master.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.core.ui.primitives.*
import cz.frantisekmasa.wfrp_master.core.viewModel.viewModel
import cz.muni.fi.rpg.ui.character.dialogs.ExperiencePointsDialog
import cz.muni.fi.rpg.ui.common.composables.AmbitionsCard
import cz.muni.fi.rpg.ui.common.composables.FloatingActionsMenu
import cz.muni.fi.rpg.ui.common.composables.MenuState
import cz.muni.fi.rpg.ui.gameMaster.rolls.Roll
import cz.muni.fi.rpg.ui.gameMaster.rolls.RollResult
import cz.muni.fi.rpg.ui.gameMaster.rolls.TestResultScreen
import cz.muni.fi.rpg.viewModels.CharacterStatsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.parameter.parametersOf

@Composable
internal fun CharacterCharacteristicsScreen(
    characterId: CharacterId,
    character: Character,
    party: Party,
    modifier: Modifier = Modifier,
) {
    val viewModel: CharacterStatsViewModel by viewModel { parametersOf(characterId) }

    var roll: Roll? by rememberSaveable { mutableStateOf(null) }

    roll?.let { currentRoll ->
        FullScreenDialog(onDismissRequest = { roll = null }) {
            val rollSound = rememberSoundPlayer(R.raw.roll_sound)

            LaunchedEffect(Unit) {
                withContext(Dispatchers.Main) { rollSound.play() }
            }

            TestResultScreen(
                testName = stringResource(R.string.title_roll),
                results = listOf(
                    RollResult(
                        characterId.toString(),
                        character.getName(),
                        currentRoll,
                    )
                ),
                onRerollRequest = {
                    roll = currentRoll.reroll()
                    rollSound.play()
                },
                onDismissRequest = { roll = null })
        }
    }

    Scaffold(
        modifier = modifier.background(MaterialTheme.colors.background),
        floatingActionButton = {
            var menuState by remember { mutableStateOf(MenuState.COLLAPSED) }

            FloatingActionsMenu(
                state = menuState,
                onToggleRequest = { menuState = it },
                iconRes = R.drawable.ic_dice_roll,
            ) {
                for (dice in listOf("1d100", "1d10")) {
                    ExtendedFloatingActionButton(
                        text = { Text(dice) },
                        onClick = {
                            menuState = MenuState.COLLAPSED

                            val expression = Expression.fromString(dice)
                            roll = Roll.Generic(expression, expression.evaluate())
                        }
                    )
                }
            }
        }
    ) {
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(bottom = Spacing.bottomPaddingUnderFab),
        ) {
            val points = character.getPoints()

            val coroutineScope = rememberCoroutineScope()

            CharacterTopPanel(
                character,
                points,
                onUpdate = { coroutineScope.launch(Dispatchers.IO) { viewModel.updatePoints(it) } }
            )

            CharacteristicsCard(character.getCharacteristics())

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = Spacing.small)
                    .padding(horizontal = Spacing.responsiveBodyPadding())
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(Spacing.gutterSize()),
            ) {
                ExperiencePointsCard(
                    points = points,
                    save = viewModel::updatePoints,
                    modifier = Modifier.weight(1f),
                )

                CareerCard(character, Modifier.weight(1f))

                SocialStatusCard(
                    character.getStatus(),
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }

            Container(Modifier.padding(top = Spacing.tiny)) {
                val size = if (breakpoint > Breakpoint.XSmall) HalfWidth else FullWidth

                column(size) {
                    AmbitionsCard(
                        titleRes = R.string.title_character_ambitions,
                        ambitions = character.getAmbitions(),
                        onSave = { viewModel.updateCharacterAmbitions(it) },
                    )
                }

                column(size) {
                    AmbitionsCard(
                        titleRes = R.string.title_party_ambitions,
                        ambitions = party.getAmbitions(),
                        titleIconRes = R.drawable.ic_group,
                        onSave = null,
                    )
                }
            }

        }
    }
}

@Composable
private fun CharacterTopPanel(character: Character, points: Points, onUpdate: (Points) -> Unit) {
    TopPanel {
        Container(
            Modifier.padding(vertical = Spacing.large, horizontal = Spacing.extraLarge),
            verticalArrangement = Arrangement.spacedBy(Spacing.large),
        ) {
            val size = if (breakpoint >= Breakpoint.Small) HalfWidth else FullWidth

            column(size) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row {
                        ItemIcon(R.drawable.ic_face, size = ItemIcon.Size.Large)
                        Column(Modifier.padding(start = Spacing.medium)) {
                            Text(character.getName(), fontWeight = FontWeight.Bold)
                            Text(
                                stringResource(character.getRace().getReadableNameId())
                                    + " "
                                    + character.getCareer(),
                                style = MaterialTheme.typography.caption
                            )
                        }
                    }

                    WoundsBadge(points, update = onUpdate)
                }
            }

            column(size) { PointsRow(points, update = onUpdate) }
        }
    }
}

@Composable
private fun WoundsBadge(points: Points, update: (Points) -> Unit) {
    var dialogVisible by remember { mutableStateOf(false) }

    Button(onClick = { dialogVisible = true }) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("${points.wounds} / ${points.maxWounds}")
            Text(
                stringResource(R.string.label_wounds),
                fontWeight = FontWeight.Normal
            )
        }
    }

    if (!dialogVisible) {
        return
    }

    PointsDialog(onDismissRequest = { dialogVisible = false }) {
        val wounds = points.wounds

        NumberPicker(
            label = stringResource(R.string.label_wounds),
            value = wounds,
            onIncrement = {
                if (wounds < points.maxWounds) {
                    update(points.copy(wounds = wounds + 1))
                }
            },
            onDecrement = {
                if (wounds > 0) {
                    update(points.copy(wounds = wounds - 1))
                }
            }
        )
    }
}

@Composable
private fun PointsDialog(onDismissRequest: () -> Unit, content: @Composable BoxScope.() -> Unit) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(shape = MaterialTheme.shapes.medium) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(Spacing.extraLarge),
                contentAlignment = Alignment.Center,
                content = content,
            )
        }
    }
}

@Composable
private fun PointsRow(points: Points, update: (Points) -> Unit) {
    Row(Modifier.fillMaxWidth()) {
        val pools =
            listOf(PointPool.FATE, PointPool.FORTUNE, PointPool.RESOLVE, PointPool.RESILIENCE)

        val modifier = Modifier.weight(1f)

        pools.forEach { pool ->
            var dialogVisible by remember { mutableStateOf(false) }
            val value = points.get(pool)

            Column(
                modifier.clickable { dialogVisible = true },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    value.toString(),
                    style = MaterialTheme.typography.h6
                )
                Text(
                    stringResource(pool.nameRes),
                    style = MaterialTheme.typography.caption,
                )
            }

            if (dialogVisible) {
                PointsDialog(onDismissRequest = { dialogVisible = false }) {
                    NumberPicker(
                        label = stringResource(pool.nameRes),
                        value = value,
                        onIncrement = { points.modify(pool, +1).onSuccess(update) },
                        onDecrement = { points.modify(pool, -1).onSuccess(update) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CardRow(modifier: Modifier = Modifier, content: @Composable RowScope.() -> Unit) {
    Surface(modifier = modifier, elevation = 2.dp) {
        Row(
            Modifier.padding(horizontal = Spacing.large, vertical = Spacing.large),
            content = content,
        )
    }
}

@Composable
private fun CharacteristicsCard(values: Stats) {
    CardRow {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.small)) {
            val breakpoint = LocalBreakpoint.current
            val characteristics = remember(breakpoint) {
                listOf(
                    Characteristic.WEAPON_SKILL,
                    Characteristic.BALLISTIC_SKILL,
                    Characteristic.STRENGTH,
                    Characteristic.TOUGHNESS,
                    Characteristic.INITIATIVE,
                    Characteristic.AGILITY,
                    Characteristic.DEXTERITY,
                    Characteristic.INTELLIGENCE,
                    Characteristic.WILL_POWER,
                    Characteristic.FELLOWSHIP,
                ).let { it.chunked(if (breakpoint > Breakpoint.XSmall) it.size else it.size / 2) }
            }

            characteristics.forEach { characteristicInRow ->
                Row(Modifier.fillMaxWidth()) {
                    characteristicInRow.forEach { characteristic ->
                        Column(
                            Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                characteristic.getShortcutName(),
                                style = MaterialTheme.typography.caption,
                            )
                            Text(
                                values.get(characteristic).toString(),
                                style = MaterialTheme.typography.h6,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CardSection(modifier: Modifier, content: @Composable ColumnScope.() -> Unit) {
    Card(modifier, elevation = 2.dp) {
        Column(Modifier.padding(Spacing.responsiveBodyPadding()), content = content)
    }
}

@Composable
private fun ExperiencePointsCard(
    points: Points,
    save: suspend (Points) -> Unit,
    modifier: Modifier,
) {
    var experiencePointsDialogVisible by rememberSaveable { mutableStateOf(false) }

    if (experiencePointsDialogVisible) {
        ExperiencePointsDialog(
            value = points,
            save = save,
            onDismissRequest = { experiencePointsDialogVisible = false },
        )
    }

    CardSection(modifier.clickable(onClick = { experiencePointsDialogVisible = true })) {
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.tiny)) {
            Text(points.experience.toString(), fontWeight = FontWeight.Bold)
            Text(stringResource(R.string.xp_points))
        }

        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                stringResource(R.string.xp_points_spent, points.spentExperience),
                style = MaterialTheme.typography.caption,
            )
        }
    }
}

@Composable
private fun CareerCard(character: Character, modifier: Modifier) {
    CardSection(modifier) {
        Text(character.getCareer(), fontWeight = FontWeight.Bold)
        Text(character.getSocialClass(), style = MaterialTheme.typography.caption)
    }
}

@Composable
private fun SocialStatusCard(status: SocialStatus, modifier: Modifier) {
    CardSection(modifier) {
        Row(Modifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painterResource(R.drawable.ic_social_status),
                "Social status icon",
                Modifier.width(Spacing.extraLarge),
            )
            Text(stringResource(status.tier.nameRes) + " ${status.standing}")
        }
    }
}
