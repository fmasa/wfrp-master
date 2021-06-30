package cz.muni.fi.rpg.ui.character

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.muni.fi.rpg.R
import cz.frantisekmasa.wfrp_master.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.core.domain.character.Points
import cz.frantisekmasa.wfrp_master.core.domain.Stats
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.core.ui.components.CharacteristicsTable
import cz.frantisekmasa.wfrp_master.core.domain.Expression
import cz.frantisekmasa.wfrp_master.core.media.rememberSoundPlayer
import cz.frantisekmasa.wfrp_master.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.core.ui.primitives.CardContainer
import cz.frantisekmasa.wfrp_master.core.ui.primitives.NumberPicker
import cz.frantisekmasa.wfrp_master.core.ui.primitives.Spacing
import cz.muni.fi.rpg.ui.common.composables.Theme
import cz.frantisekmasa.wfrp_master.core.viewModel.viewModel
import cz.muni.fi.rpg.ui.common.composables.FloatingActionsMenu
import cz.muni.fi.rpg.ui.common.composables.MenuState
import cz.muni.fi.rpg.ui.gameMaster.rolls.Roll
import cz.muni.fi.rpg.ui.gameMaster.rolls.RollResult
import cz.muni.fi.rpg.ui.gameMaster.rolls.TestResultScreen
import cz.muni.fi.rpg.viewModels.CharacterStatsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.parameter.parametersOf
import timber.log.Timber
import java.lang.IllegalArgumentException

@Composable
internal fun CharacterCharacteristicsScreen(
    characterId: CharacterId,
    character: Character,
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
                .padding(top = Spacing.small, bottom = Spacing.bottomPaddingUnderFab),
        ) {
            PointsSection(character.getPoints()) { points -> viewModel.updatePoints { points } }
            CharacteristicsSection(character.getCharacteristics())
            Spacer(Modifier.padding(bottom = 8.dp))
        }
    }
}

@Composable
private fun PointsSection(points: Points, onUpdate: (Points) -> Unit) {
    val updateIfChanged = { mutation: (Points) -> Points ->
        try {
            onUpdate(mutation(points))
        } catch (e: IllegalArgumentException) {
            Timber.d(e)
        }
    }

    Column {
        CardContainer(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                val modifier = Modifier.weight(1f)

                PointItem(
                    R.string.label_wounds,
                    points.wounds,
                    modifier = modifier,
                    color = if (points.isHeavilyWounded()) Theme.fixedColors.danger else MaterialTheme.colors.onSurface
                ) { newValue ->
                    updateIfChanged { it.copy(wounds = newValue) }
                }

                PointItem(
                    R.string.label_corruption,
                    points.corruption,
                    modifier = modifier,
                ) { newValue ->
                    updateIfChanged { it.copy(corruption = newValue) }
                }
            }
        }

        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
        ) {
            CardContainer(Modifier.weight(1f)) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        stringResource(R.string.label_fate_points),
                        modifier = Modifier.padding(bottom = 10.dp),
                        style = MaterialTheme.typography.h6
                    )

                    PointItem(R.string.label_fate_points, points.fate) { newValue ->
                        updateIfChanged { it.withFate(newValue) }
                    }

                    PointItem(R.string.label_fortune_points, points.fortune) { newValue ->
                        updateIfChanged { it.copy(fortune = newValue) }
                    }
                }
            }

            CardContainer(Modifier.weight(1f)) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        stringResource(R.string.label_resilience),
                        modifier = Modifier.padding(bottom = 10.dp),
                        style = MaterialTheme.typography.h6
                    )

                    PointItem(R.string.label_resilience, points.resilience) { newValue ->
                        updateIfChanged { it.withResilience(newValue) }
                    }

                    PointItem(R.string.label_resolve, points.resolve) { newValue ->
                        updateIfChanged { it.copy(resolve = newValue) }
                    }
                }
            }
        }
    }
}

@Composable
private fun PointItem(
    @StringRes labelRes: Int,
    value: Int,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.onSurface,
    onUpdate: (Int) -> Unit,
) {
    NumberPicker(
        label = stringResource(labelRes),
        value = value,
        color = color,
        onIncrement = { onUpdate(value + 1) },
        onDecrement = { onUpdate(value - 1) },
        modifier = modifier,
    )
}

@Composable
private fun CharacteristicsSection(stats: Stats) {
    CardContainer(modifier = Modifier.padding(horizontal = 8.dp)) {
        CharacteristicsTable(stats)
    }
}
