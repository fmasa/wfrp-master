package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FixedViewModelFactory(private val viewModel: ViewModel ): ViewModelProvider.NewInstanceFactory()
{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = viewModel as T
}
