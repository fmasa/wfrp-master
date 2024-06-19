package cz.frantisekmasa.wfrp_master.common.character.wellBeing

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.character.wellBeing.diseases.DiseaseItem
import cz.frantisekmasa.wfrp_master.common.character.wellBeing.diseases.diseasesCard
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardContainer
import cz.frantisekmasa.wfrp_master.common.core.ui.responsive.Breakpoint
import cz.frantisekmasa.wfrp_master.common.core.ui.responsive.LocalBreakpoint
import kotlinx.collections.immutable.ImmutableList

@Composable
fun WellBeingScreen(
    characterId: CharacterId,
    modifier: Modifier = Modifier,
    state: WellBeingScreenState,
    removeDisease: (DiseaseItem) -> Unit,
) {
    val diseasesCard: LazyListScope.() -> Unit = {
        diseasesCard(
            characterId = characterId,
            diseases = state.diseases,
            onRemoveRequest = removeDisease,
        )
    }

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

data class WellBeingScreenState(
    val corruptionPoints: CorruptionPoints,
    val diseases: ImmutableList<DiseaseItem>,
)

data class CorruptionPoints(
    val current: Int,
    val buffer: Int,
)
