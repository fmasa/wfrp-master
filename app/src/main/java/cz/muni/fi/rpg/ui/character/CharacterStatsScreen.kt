package cz.muni.fi.rpg.ui.character

import androidx.annotation.StringRes
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.Character
import cz.muni.fi.rpg.model.domain.character.Points
import cz.muni.fi.rpg.model.domain.character.Stats
import cz.muni.fi.rpg.ui.common.composables.CardContainer
import cz.muni.fi.rpg.ui.common.composables.NumberPicker
import cz.muni.fi.rpg.ui.common.composables.Theme
import cz.muni.fi.rpg.viewModels.CharacterStatsViewModel
import timber.log.Timber
import java.lang.IllegalArgumentException

@Composable
fun CharacterCharacteristicsScreen(
    character: Character,
    viewModel: CharacterStatsViewModel,
    modifier: Modifier = Modifier,
) {

    ScrollableColumn(
        modifier.background(MaterialTheme.colors.background)
    ) {
        PointsSection(character.getPoints()) { points -> viewModel.updatePoints { points } }
        CharacteristicsSection(character.getCharacteristics())
        Spacer(Modifier.padding(bottom = 8.dp))
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

    ScrollableColumn {
        CardContainer(Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
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

                PointItem(R.string.label_sin, points.sin, modifier = modifier) { newValue ->
                    updateIfChanged { it.copy(sin = newValue) }
                }
            }
        }

        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
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
    color: Color = MaterialTheme.colors.onSurface,
    modifier: Modifier = Modifier,
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
        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Characteristic(R.string.label_shortcut_weapon_skill, stats.weaponSkill)
                Characteristic(R.string.label_shortcut_agility, stats.agility)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Characteristic(R.string.label_shortcut_ballistic_skill, stats.ballisticSkill)
                Characteristic(R.string.label_shortcut_dexterity, stats.dexterity)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Characteristic(R.string.label_shortcut_strength, stats.strength)
                Characteristic(R.string.label_shortcut_intelligence, stats.intelligence)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Characteristic(R.string.label_shortcut_toughness, stats.toughness)
                Characteristic(R.string.label_shortcut_will_power, stats.willPower)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Characteristic(R.string.label_shortcut_initiative, stats.initiative)
                Characteristic(R.string.label_shortcut_fellowship, stats.fellowship)
            }
        }
    }
}

@Composable
private fun Characteristic(@StringRes labelRes: Int, value: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(stringResource(labelRes), style = MaterialTheme.typography.subtitle1)
        Text(
            value.toString(),
            Modifier.padding(vertical = 12.dp),
            style = MaterialTheme.typography.h5
        )
    }
}