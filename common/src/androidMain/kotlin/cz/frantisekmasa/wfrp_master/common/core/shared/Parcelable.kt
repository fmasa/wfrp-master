package cz.frantisekmasa.wfrp_master.common.core.shared

import android.os.Parcelable as AndroidParcelable
import kotlinx.parcelize.Parcelize as KotlinxParcelize
import java.io.Serializable

actual interface Parcelable: AndroidParcelable, Serializable

actual typealias Parcelize = KotlinxParcelize