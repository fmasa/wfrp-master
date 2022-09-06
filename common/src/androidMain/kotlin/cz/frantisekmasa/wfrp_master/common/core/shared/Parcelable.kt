package cz.frantisekmasa.wfrp_master.common.core.shared

import java.io.Serializable
import android.os.Parcelable as AndroidParcelable
import kotlinx.parcelize.Parcelize as KotlinxParcelize

actual interface Parcelable : AndroidParcelable, Serializable

actual typealias Parcelize = KotlinxParcelize
