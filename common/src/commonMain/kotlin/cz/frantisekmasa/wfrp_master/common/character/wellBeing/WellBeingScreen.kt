package cz.frantisekmasa.wfrp_master.common.character.wellBeing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.wellBeing.diseases.DiseaseItem
import cz.frantisekmasa.wfrp_master.common.character.wellBeing.diseases.diseasesCard
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardContainer
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.NumberPicker
import cz.frantisekmasa.wfrp_master.common.core.ui.responsive.Breakpoint
import cz.frantisekmasa.wfrp_master.common.core.ui.responsive.LocalBreakpoint
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.TopPanel
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun WellBeingScreen(
    characterId: CharacterId,
    modifier: Modifier = Modifier,
    state: WellBeingScreenState,
    removeDisease: (DiseaseItem) -> Unit,
    updateCharacter: suspend ((Character) -> Character) -> Unit,
) {
    val diseasesCard: LazyListScope.() -> Unit = {
        diseasesCard(
            characterId = characterId,
            diseases = state.diseases,
            onRemoveRequest = removeDisease,
        )
    }
    Column {
        CorruptionPoints(
            pool = state.corruptionPoints,
            updateCharacter = updateCharacter,
        )

        if (LocalBreakpoint.current > Breakpoint.XSmall) {
            CardContainer(Modifier) {
                LazyColumn(Modifier.fillMaxWidth()) {
                    diseasesCard()
                }
            }
        } else {
            LazyColumn(
                modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
            ) {
                diseasesCard()
            }
        }
    }
}

@Composable
private fun CorruptionPoints(
    pool: CorruptionPoints,
    updateCharacter: suspend ((Character) -> Character) -> Unit,
) {
    TopPanel {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth(),
        ) {
            val current = pool.current
            val coroutineScope = rememberCoroutineScope()
            val updateCorruptionPoints = { corruptionPoints: Int ->
                if (corruptionPoints != current) {
                    coroutineScope.launch(Dispatchers.IO) {
                        updateCharacter {
                            it.updatePoints(it.points.copy(corruption = corruptionPoints))
                        }
                    }
                }
            }

            NumberPicker(
                label = stringResource(Str.points_corruption),
                max = pool.buffer,
                value = current,
                onIncrement = { updateCorruptionPoints(current + 1) },
                onDecrement = { updateCorruptionPoints((current - 1).coerceAtLeast(0)) },
            )
        }
    }
}

data class WellBeingScreenState(
    val corruptionPoints: CorruptionPoints,
    val diseases: ImmutableList<DiseaseItem>,
)

data class CorruptionPoints(
    val current: Int,
    val buffer: Int,
)
