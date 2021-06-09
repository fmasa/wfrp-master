package cz.frantisekmasa.wfrp_master.inventory.domain.armour

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import kotlinx.parcelize.Parcelize

@Parcelize
data class ArmourPoints(
    @JsonValue
    private val value: Int
): Parcelable {
    init {
        require(value >= 0) { "Armour points cannot be negative" }
    }

    // TODO: Use Long for APs
    @JsonCreator
    constructor(value: Long) : this(value.toInt())
}