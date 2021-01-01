package cz.muni.fi.rpg.ui.common.composables

import androidx.compose.runtime.Composable
import cz.frantisekmasa.wfrp_master.core.di.AmbientKoinScope
import cz.muni.fi.rpg.viewModels.GameMasterViewModel
import org.koin.core.parameter.parametersOf
import java.util.*

object ViewModel {
    @Composable
    fun GameMaster(partyId: UUID): GameMasterViewModel
//        = viewModel<GameMasterViewModel> { parametersOf(partyId) }.value
    = AmbientKoinScope.current.get { parametersOf(partyId) }
}