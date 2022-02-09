package cz.frantisekmasa.wfrp_master.common.core.shared

import android.os.Parcelable as AndroidParcelable
import kotlinx.parcelize.Parcelize as KotlinxParcelize

actual typealias Parcelable = AndroidParcelable

actual typealias Parcelize = KotlinxParcelize