package cz.muni.fi.rpg.ui.common

import android.content.Context
import android.widget.LinearLayout
import androidx.compose.runtime.Stable
import androidx.core.os.bundleOf
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

@Stable
class AdManager(private val context: Context) {
    private var personalizedAds: Boolean = true

    fun initialize(personalizedAds: Boolean) {
        this.personalizedAds = personalizedAds

        // Setup AdMob
        MobileAds.initialize(context) { }
    }

    fun initializeUnit(view: AdView) {
        view.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            AdSize.SMART_BANNER.getHeightInPixels(context)
        )

        view.loadAd(
            AdRequest.Builder()
                .addNetworkExtrasBundle(
                    AdMobAdapter::class.java,
                    bundleOf("npa" to if (personalizedAds) "0" else "1")
                )
                .build()
        )
    }
}