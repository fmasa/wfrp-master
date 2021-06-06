package cz.frantisekmasa.wfrp_master.religion.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.core.viewModel.getViewModel
import cz.frantisekmasa.wfrp_master.religion.ui.blessings.BlessingsCard
import cz.frantisekmasa.wfrp_master.religion.ui.miracles.MiraclesCard
import org.koin.core.parameter.parametersOf

@Composable
fun CharacterReligionScreen(
    modifier: Modifier = Modifier,
    characterId: CharacterId,
) {
    Column(
        modifier
            .background(MaterialTheme.colors.background)
            .verticalScroll(rememberScrollState())
            .padding(top = Spacing.small),
    ) {
        BlessingsCard(getViewModel { parametersOf(characterId) })
        MiraclesCard(getViewModel { parametersOf(characterId) })
    }
}