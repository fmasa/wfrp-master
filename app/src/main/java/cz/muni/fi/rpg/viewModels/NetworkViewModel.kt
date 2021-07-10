package cz.muni.fi.rpg.viewModels

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import cz.frantisekmasa.wfrp_master.core.ui.viewinterop.LocalActivity
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.rx2.asFlow
import org.koin.androidx.viewmodel.ext.android.getViewModel

class NetworkViewModel(context: Context) : ViewModel() {
    val isConnectedToInternet: LiveData<Boolean> =
        ReactiveNetwork.observeNetworkConnectivity(context)
            .asFlow()
            .map { it.available() }
            .asLiveData()
}

@Composable
fun provideNetworkViewModel(): NetworkViewModel = LocalActivity.current.getViewModel()
