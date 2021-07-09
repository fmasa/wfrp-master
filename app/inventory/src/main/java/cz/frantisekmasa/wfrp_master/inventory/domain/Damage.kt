package cz.frantisekmasa.wfrp_master.inventory.domain

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonValue
import kotlinx.parcelize.Parcelize

@Parcelize
data class Damage(
    @JsonValue
    private val value: Int
) : Parcelable {
    init {
        require(value >= 0) { "Damage cannot be negative" }
    }
}
