package cz.frantisekmasa.wfrp_master.core.viewModel

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.AmbientViewModelStoreOwner
import androidx.lifecycle.ViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.ParametersDefinition

@Composable
inline fun <reified T : ViewModel> viewModel(noinline parameters: ParametersDefinition? = null): Lazy<T> {
    return AmbientViewModelStoreOwner.current.viewModel(parameters = parameters)
}
