package cz.frantisekmasa.wfrp_master.common.network

import android.content.Context
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.rx2.asFlow

class ReactiveNetworkConnectivityChecker(private val context: Context): ConnectivityChecker {
    override val availability: Flow<Boolean> by lazy {
        ReactiveNetwork.observeNetworkConnectivity(context)
            .asFlow()
            .map { it.available() }
    }
}