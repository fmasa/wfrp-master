package cz.frantisekmasa.wfrp_master.core.ads

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import cz.frantisekmasa.wfrp_master.core.viewModel.providePremiumViewModel
import cz.muni.fi.rpg.viewModels.provideSettingsViewModel

@Composable
fun BannerAd(unitId: String) {
    val premiumActive = providePremiumViewModel().active == true

    if (premiumActive) {
        return
    }

    val viewModel = provideAdViewModel()

    val size = AdSize.SMART_BANNER
    val personalizedAds = provideSettingsViewModel().personalizedAds.collectAsState()

    key(personalizedAds) {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(with(AmbientDensity.current) {
                    size
                        .getHeightInPixels(AmbientContext.current)
                        .toDp()
                }),
            viewBlock = {
                AdView(it).apply {
                    adUnitId = unitId
                    adSize = size
                    viewModel.initializeAdUnit(this)
                }
            }
        )
    }
}