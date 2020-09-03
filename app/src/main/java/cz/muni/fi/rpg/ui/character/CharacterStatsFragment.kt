package cz.muni.fi.rpg.ui.character

import android.os.Bundle
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Box
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.domain.character.Points
import cz.muni.fi.rpg.model.map
import cz.muni.fi.rpg.model.right
import cz.muni.fi.rpg.ui.common.NumberPicker
import cz.muni.fi.rpg.ui.common.parcelableArgument
import cz.muni.fi.rpg.ui.views.StatsTable
import cz.muni.fi.rpg.viewModels.CharacterStatsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber
import java.lang.IllegalArgumentException

class CharacterStatsFragment : Fragment(R.layout.fragment_character_stats),
    CoroutineScope by CoroutineScope(Dispatchers.Default) {
    companion object {
        private const val ARGUMENT_CHARACTER_ID = "CHARACTER_ID"

        fun newInstance(characterId: CharacterId) = CharacterStatsFragment().apply {
            arguments = bundleOf(ARGUMENT_CHARACTER_ID to characterId)
        }
    }

    private val characterId: CharacterId by parcelableArgument(ARGUMENT_CHARACTER_ID)
    private val viewModel: CharacterStatsViewModel by viewModel { parametersOf(characterId) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindCompose(view)
        bindStats(view)
    }

    private fun bindCompose(view: View) {
        view.findViewById<ComposeView>(R.id.compose).setContent {
            MaterialTheme {
                val character = viewModel.character.right().observeAsState()

                character.value?.let {
                    Box(Modifier.padding(horizontal = 8.dp)) {
                        PointsSection(it.getPoints()) { points -> viewModel.updatePoints { points } }
                    }
                }
            }
        }
    }

    private fun bindStats(view: View) {
        viewModel.character
            .right()
            .map { character -> character.getCharacteristics() }
            .observe(viewLifecycleOwner) {
                view.findViewById<StatsTable>(R.id.statsTable).setValue(it)
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
        CardContainer(Modifier.fillMaxWidth()) {
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                PointItem(
                    R.string.label_wounds,
                    points.wounds,
                    color = if (points.isHeavilyWounded()) R.color.colorDanger else R.color.colorText
                ) { newValue ->
                    updateIfChanged { it.copy(wounds = newValue) }
                }

                PointItem(R.string.label_corruption, points.corruption) { newValue ->
                    updateIfChanged { it.copy(corruption = newValue) }
                }

                PointItem(R.string.label_sin, points.sin) { newValue ->
                    updateIfChanged { it.copy(sin = newValue) }
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)) {
            CardContainer(Modifier.weight(1f)) {
                Column(
                    horizontalGravity = Alignment.CenterHorizontally,
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
                    horizontalGravity = Alignment.CenterHorizontally,
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

// TODO: Move to common Composables
@Composable
private fun CardContainer(modifier: Modifier? = null, content: @Composable () -> Unit) {
    val baseModifier = Modifier.padding(top = 12.dp)

    Box(modifier?.let { baseModifier.then(it) } ?: baseModifier) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = 2.dp,
            shape = RoundedCornerShape(4.dp)
        ) {
            Box(Modifier.padding(vertical = 16.dp, horizontal = 8.dp), children = content)
        }
    }
}

@Composable
private fun PointItem(
    @StringRes labelRes: Int,
    value: Int,
    @ColorRes color: Int = R.color.colorText,
    onUpdate: (Int) -> Unit
) {
    NumberPicker(
        label = stringResource(labelRes),
        value = value,
        color = color,
        onIncrement = { onUpdate(value + 1) },
        onDecrement = { onUpdate(value - 1) }
    )
}