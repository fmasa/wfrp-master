package cz.muni.fi.rpg.ui.character

import androidx.annotation.StringRes
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.core.domain.Characteristic
import cz.muni.fi.rpg.R
import cz.frantisekmasa.wfrp_master.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.core.domain.character.Points
import cz.frantisekmasa.wfrp_master.core.domain.Stats
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.core.ui.components.CharacteristicsTable
import cz.frantisekmasa.wfrp_master.core.ui.primitives.CardContainer
import cz.frantisekmasa.wfrp_master.core.ui.primitives.NumberPicker
import cz.muni.fi.rpg.ui.common.composables.Theme
import cz.frantisekmasa.wfrp_master.core.viewModel.viewModel
import cz.muni.fi.rpg.viewModels.CharacterStatsViewModel
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

    Column {
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
