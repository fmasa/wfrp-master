package cz.frantisekmasa.wfrp_master.common.core.domain.time

import androidx.compose.runtime.Immutable
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
@Immutable
data class DateTime(
    @SerialName("imperialDay")
    val date: ImperialDate,
    private val minutes: Int,
) : Parcelable {
    companion object {
        private fun toMinutes(time: TimeOfDay) = time.hour * 60 + time.minute
    }

    init {
        require(minutes in 0 until 60 * 24)
    }

    val time: TimeOfDay
        get() = TimeOfDay(minutes / 60, minutes % 60)

    @Parcelize
    @Immutable
    data class TimeOfDay(val hour: Int, val minute: Int) : Parcelable {
        fun format() = hour.toString().padStart(2, '0') + ":" + minute.toString().padStart(2, '0')

        init {
            require(hour in 0 until 24)
            require(minute in 0 until 60)
        }
    }

    constructor(date: ImperialDate, time: TimeOfDay) : this(date, toMinutes(time))

    fun withTime(time: TimeOfDay): DateTime = copy(minutes = toMinutes(time))
}
