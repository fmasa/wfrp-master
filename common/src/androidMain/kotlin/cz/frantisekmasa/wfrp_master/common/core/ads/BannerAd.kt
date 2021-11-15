package cz.frantisekmasa.wfrp_master.common.core.ads

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.viewModel.providePremiumViewModel
import cz.frantisekmasa.wfrp_master.common.core.viewModel.provideSettingsViewModel

@Composable
fun BannerAd(unitId: String) {
    val premiumActive = providePremiumViewModel().active == true

    if (premiumActive) {
        return
    }

    val viewModel = provideAdViewModel()

    val size = AdSize.SMART_BANNER
    val personalizedAds = provideSettingsViewModel().personalizedAds.collectWithLifecycle(false)

    key(personalizedAds) {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(
                    with(LocalDensity.current) {
                        size
                            .getHeightInPixels(LocalContext.current)
                            .toDp()
                    }
                ),
            factory = {
                AdView(it).apply {
                    adUnitId = unitId
                    adSize = size
                    viewModel.initializeAdUnit(this)
                }
            }
        )
    }
}
