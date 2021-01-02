package cz.frantisekmasa.wfrp_master.core.viewModel

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.AmbientViewModelStoreOwner
import androidx.lifecycle.ViewModel
import cz.frantisekmasa.wfrp_master.core.di.AmbientKoinScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.parameter.parametersOf

@Deprecated("Use newViewModel instead")
@Composable
inline fun <reified T : ViewModel> viewModel(noinline parameters: ParametersDefinition? = null): Lazy<T> {
    return AmbientViewModelStoreOwner.current.viewModel(parameters = parameters)
}

@Composable
inline fun <reified T> newViewModel(noinline parameters: ParametersDefinition? = null): T =
    AmbientKoinScope.current.get(parameters = parameters)