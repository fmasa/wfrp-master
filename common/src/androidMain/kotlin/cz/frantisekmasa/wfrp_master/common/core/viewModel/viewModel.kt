package cz.frantisekmasa.wfrp_master.common.core.viewModel

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.ParametersDefinition

@Composable
inline fun <reified T : ViewModel> viewModel(noinline parameters: ParametersDefinition? = null): Lazy<T> {
    return LocalViewModelStoreOwner.current!!.viewModel(parameters = parameters)
}

@Composable
inline fun <reified T : ViewModel> getViewModel(noinline parameters: ParametersDefinition? = null): T {
    return LocalViewModelStoreOwner.current!!.getViewModel(parameters = parameters)
}
