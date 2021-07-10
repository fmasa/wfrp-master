package cz.muni.fi.rpg.ui.common.composables

import androidx.compose.runtime.Composable
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.core.viewModel.viewModel
import cz.muni.fi.rpg.viewModels.GameMasterViewModel
import org.koin.core.parameter.parametersOf

object ViewModel {
    @Composable
    fun GameMaster(partyId: PartyId): GameMasterViewModel =
        viewModel<GameMasterViewModel> { parametersOf(partyId) }.value
}
