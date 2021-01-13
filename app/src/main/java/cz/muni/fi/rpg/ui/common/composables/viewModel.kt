package cz.muni.fi.rpg.ui.common.composables

import androidx.compose.runtime.Composable
import cz.frantisekmasa.wfrp_master.core.di.AmbientKoinScope
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyId
import cz.muni.fi.rpg.viewModels.GameMasterViewModel
import org.koin.core.parameter.parametersOf

object ViewModel {
    @Composable
    fun GameMaster(partyId: PartyId): GameMasterViewModel
    = AmbientKoinScope.current.get { parametersOf(partyId) }
}