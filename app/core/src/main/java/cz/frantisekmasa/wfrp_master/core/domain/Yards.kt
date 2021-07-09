package cz.frantisekmasa.wfrp_master.core.domain

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonValue
import kotlinx.parcelize.Parcelize

@Parcelize
data class Yards(
    @JsonValue
    private val value: Int
) : Parcelable {
    init {
        require(value >= 0) { "Yards cannot be negative" }
    }
}
