package cz.muni.fi.rpg.ui.common.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ViewModelStoreOwnerAmbient
import androidx.lifecycle.ViewModel
import cz.muni.fi.rpg.viewModels.GameMasterViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.context.GlobalContext
import org.koin.core.parameter.ParametersDefinition
import java.util.*

@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated("Using ViewModelStoreOwner seems broken. Use explicit factory in ViewModel object")
@Composable
inline fun <reified T : ViewModel> viewModel(noinline parameters: ParametersDefinition? = null): Lazy<T> {
    return ViewModelStoreOwnerAmbient.current.viewModel(parameters = parameters)
}

object ViewModel {
    @Composable
    fun GameMaster(partyId: UUID): GameMasterViewModel = remember(partyId) {
        GameMasterViewModel(partyId, get(), get())
    }

    private inline fun <reified T> get(): T = GlobalContext.get().get()
}