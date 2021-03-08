package cz.frantisekmasa.wfrp_master.core.ads

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import com.google.android.gms.ads.AdView
import cz.frantisekmasa.wfrp_master.core.ui.viewinterop.LocalActivity
import org.koin.androidx.viewmodel.ext.android.getViewModel

class AdViewModel(private val adManager: AdManager) : ViewModel() {
    fun initializeAdUnit(view: AdView) {
        adManager.initializeUnit(view)
    }
}

@Composable
fun provideAdViewModel(): AdViewModel = LocalActivity.current.getViewModel()