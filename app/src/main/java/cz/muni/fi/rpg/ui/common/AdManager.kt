package cz.muni.fi.rpg.ui.common

import android.content.Context
import androidx.core.os.bundleOf
import com.google.ads.consent.ConsentInfoUpdateListener
import com.google.ads.consent.ConsentInformation
import com.google.ads.consent.ConsentStatus
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

class AdManager(private val context: Context) {
    private var showNonPersonalizedAdsOnly: Boolean = true

    fun initialize() {
        // Setup AdMob
        MobileAds.initialize(context) { }

        val consentInformation = ConsentInformation.getInstance(context)
        val publisherIds = arrayOf("pub-0123456789012345")

        consentInformation.requestConsentInfoUpdate(
            publisherIds,
            object : ConsentInfoUpdateListener {
                override fun onConsentInfoUpdated(consentStatus: ConsentStatus) {
                    // We do not ask EEA users for consent for data processing related to personalized
                    // ads. We always show non-personalized ads to them!
                    showNonPersonalizedAdsOnly = consentInformation.isRequestLocationInEeaOrUnknown
                }

                override fun onFailedToUpdateConsentInfo(errorDescription: String) {
                    showNonPersonalizedAdsOnly = true
                }
            })

    }

    fun initializeUnit(view: AdView) {
        view.loadAd(
            AdRequest.Builder()
                .addNetworkExtrasBundle(
                    AdMobAdapter::class.java,
                    bundleOf("npa" to if (showNonPersonalizedAdsOnly) "1" else "0")
                )
                .build()
        )
    }
}