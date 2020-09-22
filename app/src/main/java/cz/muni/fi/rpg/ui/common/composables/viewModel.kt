package cz.muni.fi.rpg.ui.common.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ViewModelStoreOwnerAmbient
import androidx.lifecycle.ViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.ParametersDefinition

@Composable
inline fun <reified T : ViewModel> viewModel(noinline parameters: ParametersDefinition): Lazy<T> {
    return ViewModelStoreOwnerAmbient.current.viewModel<T>(parameters = parameters)
}
