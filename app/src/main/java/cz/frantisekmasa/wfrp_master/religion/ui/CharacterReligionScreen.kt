package cz.frantisekmasa.wfrp_master.religion.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.NumberPicker
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.TopPanel
import cz.frantisekmasa.wfrp_master.common.core.viewModel.getViewModel
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import cz.frantisekmasa.wfrp_master.religion.ui.blessings.BlessingsCard
import cz.frantisekmasa.wfrp_master.religion.ui.miracles.MiraclesCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.parameter.parametersOf

@Composable
fun CharacterReligionScreen(
    modifier: Modifier = Modifier,
    characterId: CharacterId,
    character: Character,
    updateCharacter: suspend ((Character) -> Unit) -> Unit,
) {
    Column(
        modifier
            .background(MaterialTheme.colors.background)
            .verticalScroll(rememberScrollState())
    ) {
        SinPoints(character, updateCharacter)

        BlessingsCard(getViewModel { parametersOf(characterId) })
        MiraclesCard(getViewModel { parametersOf(characterId) })
    }
}

@Composable
private fun SinPoints(
    character: Character,
    updateCharacter: suspend ((Character) -> Unit) -> Unit,
) {
    TopPanel {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            val points = character.getPoints()
            val coroutineScope = rememberCoroutineScope()
            val updateSinPoints = { sinPoints: Int ->
                if (sinPoints != points.sin) {
                    coroutineScope.launch(Dispatchers.IO) {
                        updateCharacter {
                            it.updatePoints(points.copy(sin = sinPoints))
                        }
                    }
                }
            }

            val sinPoints = points.sin

            NumberPicker(
                label = LocalStrings.current.points.labelSinPoints,
                value = sinPoints,
                onIncrement = { updateSinPoints(sinPoints + 1) },
                onDecrement = { updateSinPoints((sinPoints - 1).coerceAtLeast(0)) }
            )
        }
    }
}
