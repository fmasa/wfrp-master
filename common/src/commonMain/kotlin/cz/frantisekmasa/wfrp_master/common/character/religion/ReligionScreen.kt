package cz.frantisekmasa.wfrp_master.common.character.religion

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
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.religion.blessings.BlessingsCard
import cz.frantisekmasa.wfrp_master.common.character.religion.blessings.BlessingsScreenModel
import cz.frantisekmasa.wfrp_master.common.character.religion.miracles.MiraclesCard
import cz.frantisekmasa.wfrp_master.common.character.religion.miracles.MiraclesScreenModel
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.NumberPicker
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.TopPanel
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ReligionScreen(
    modifier: Modifier = Modifier,
    character: Character,
    updateCharacter: suspend ((Character) -> Character) -> Unit,
    blessingsScreenModel: BlessingsScreenModel,
    miraclesScreenModel: MiraclesScreenModel,
) {
    Column(
        modifier
            .background(MaterialTheme.colors.background)
            .verticalScroll(rememberScrollState())
    ) {
        SinPoints(character, updateCharacter)

        BlessingsCard(blessingsScreenModel)
        MiraclesCard(miraclesScreenModel)
    }
}

@Composable
private fun SinPoints(
    character: Character,
    updateCharacter: suspend ((Character) -> Character) -> Unit,
) {
    TopPanel {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            val points = character.points
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
                label = stringResource(Str.points_label_sin_points),
                value = sinPoints,
                onIncrement = { updateSinPoints(sinPoints + 1) },
                onDecrement = { updateSinPoints((sinPoints - 1).coerceAtLeast(0)) }
            )
        }
    }
}
