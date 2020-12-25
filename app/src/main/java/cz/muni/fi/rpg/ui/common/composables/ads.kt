package cz.muni.fi.rpg.ui.common.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import cz.muni.fi.rpg.ui.common.AdManager

@Composable
fun BannerAd(unitId: String, adManager: AdManager) {
    val size = AdSize.SMART_BANNER
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(with(AmbientDensity.current) {
                size.getHeightInPixels(AmbientContext.current).toDp()
            }),
        viewBlock = {
            AdView(it).apply {
                adUnitId = unitId
                adSize = size
                adManager.initializeUnit(this)
            }
        }
    )
}