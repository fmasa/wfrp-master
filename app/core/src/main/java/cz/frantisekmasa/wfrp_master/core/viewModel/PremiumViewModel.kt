package cz.frantisekmasa.wfrp_master.core.viewModel

import android.app.Activity
import androidx.annotation.UiThread
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import arrow.core.extensions.list.foldable.exists
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.revenuecat.purchases.*
import cz.frantisekmasa.wfrp_master.core.auth.UserId
import cz.frantisekmasa.wfrp_master.core.ui.viewinterop.AmbientActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.getViewModel
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class PremiumViewModel(private val purchases: Purchases) : ViewModel() {
    companion object {
        const val FREE_PARTY_COUNT = 1
        const val FREE_ENCOUNTER_COUNT = 3

        const val premiumProductId = "premium"
    }

    var active by mutableStateOf<Boolean?>(null)
        private set

    suspend fun refreshPremiumForUser(userId: UserId) {
        val success: Boolean = suspendCoroutine { continuation ->
            purchases.identifyWith(
                userId.toString(),
                {
                    Timber.e("\"${it.message}\" caused by \"${it.underlyingErrorMessage}\"")

                    continuation.resume(false)
                },
                {
                    continuation.resume(true)
                }
            )
        }

        if (!success) {
            return
        }

        refreshPremiumStatus()
    }

    suspend fun purchasePremium(activity: Activity): PurchaseResult {
        val offering = offering() ?: return PurchaseResult.OfferingNotAvailable

        val result = withContext(Dispatchers.Main) {
            purchasePackage(activity, offering.availablePackages[0])
        }

        refreshPremiumStatus()

        return result
    }

    private suspend fun refreshPremiumStatus() {
        active = suspendCoroutine { continuation ->
            purchases.getPurchaserInfoWith(
                { continuation.resume(null) },
                { info ->
                    continuation.resume(
                        info.nonSubscriptionTransactions.exists { it.productId == premiumProductId }
                    )
                }
            )
        }
    }
    private suspend fun offering(): Offering? = suspendCoroutine { continuation ->
        purchases.getOfferingsWith(
            { continuation.resume(null) },
            { continuation.resume(it.current) }
        )
    }

    @UiThread
    private suspend fun purchasePackage(
        activity: Activity,
        packageToPurchase: Package
    ): PurchaseResult = suspendCoroutine { continuation ->
        purchases.purchasePackageWith(
            activity,
            packageToPurchase,
            { error, userCancelled ->
                continuation.resume(
                    if (userCancelled)
                        PurchaseResult.CanceledByUser
                    else PurchaseResult.Error(error)
                )
            },
            { _, _ ->
                Firebase.analytics.logEvent("premium_purchase", null)
                continuation.resume(PurchaseResult.Success)
            }
        )
    }
}

@Composable
fun providePremiumViewModel(): PremiumViewModel = AmbientActivity.current.getViewModel()

sealed class PurchaseResult {
    object Success : PurchaseResult()
    object CanceledByUser : PurchaseResult()
    object OfferingNotAvailable : PurchaseResult()
    data class Error(val error: PurchasesError?) : PurchaseResult()
}