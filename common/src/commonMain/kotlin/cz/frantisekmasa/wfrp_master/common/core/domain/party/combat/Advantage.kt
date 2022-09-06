package cz.frantisekmasa.wfrp_master.common.core.domain.party.combat

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@JvmInline
@Immutable
@Serializable
value class Advantage(val value: Int) : Comparable<Advantage> {

    init {
        require(value >= 0) { "Advantage cannot be negative" }
    }

    operator fun inc(): Advantage = Advantage(value.inc())

    operator fun dec(): Advantage {
        if (this == ZERO) {
            return ZERO
        }

        return Advantage(value.dec())
    }

    override fun compareTo(other: Advantage): Int = value.compareTo(other.value)
    override fun toString(): String = value.toString()

    companion object {
        val ZERO = Advantage(0)
    }
}
