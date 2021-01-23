package cz.muni.fi.rpg.ui.premium

import com.revenuecat.purchases.PurchasesError

class PremiumPurchaseFailed(message: String) : Exception(message) {
    constructor(error: PurchasesError): this(error.toString())
}