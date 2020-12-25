package cz.muni.fi.rpg.ui.common.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ViewModelStoreOwnerAmbient
import androidx.lifecycle.ViewModel
import cz.muni.fi.rpg.viewModels.GameMasterViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.GlobalContext
import org.koin.core.parameter.ParametersDefinition
import java.util.*

object ViewModel {
    @Composable
    fun GameMaster(partyId: UUID): GameMasterViewModel = remember(partyId) {
        GameMasterViewModel(partyId, get(), get())
    }

    private inline fun <reified T> get(): T = GlobalContext.get().get()
}