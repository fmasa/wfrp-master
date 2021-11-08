package cz.frantisekmasa.wfrp_master.core.viewModel

import android.app.Activity
import androidx.annotation.UiThread
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.revenuecat.purchases.Offering
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesError
import com.revenuecat.purchases.getOfferingsWith
import com.revenuecat.purchases.getPurchaserInfoWith
import com.revenuecat.purchases.identifyWith
import com.revenuecat.purchases.purchasePackageWith
import cz.frantisekmasa.wfrp_master.core.auth.UserId
import cz.frantisekmasa.wfrp_master.core.ui.viewinterop.LocalActivity
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.getViewModel
import java.util.UUID
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
                    Napier.e("\"${it.message}\" caused by \"${it.underlyingErrorMessage}\"")

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
                        info.nonSubscriptionTransactions.any { it.productId == premiumProductId }
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
        val purchaseId = UUID.randomUUID()
        Firebase.analytics.logEvent("premium_purchase_opened") {
            param("id", purchaseId.toString())
        }

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
                Firebase.analytics.logEvent("premium_purchase") {
                    param("id", purchaseId.toString())
                }
                continuation.resume(PurchaseResult.Success)
            }
        )
    }
}

@Composable
fun providePremiumViewModel(): PremiumViewModel = LocalActivity.current.getViewModel()

sealed class PurchaseResult {
    object Success : PurchaseResult()
    object CanceledByUser : PurchaseResult()
    object OfferingNotAvailable : PurchaseResult()
    data class Error(val error: PurchasesError?) : PurchaseResult()
}
