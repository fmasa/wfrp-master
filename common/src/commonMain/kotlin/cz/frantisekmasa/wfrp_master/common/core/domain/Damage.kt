package cz.frantisekmasa.wfrp_master.common.core.domain

import androidx.compose.runtime.Immutable
import kotlin.jvm.JvmInline

@JvmInline
@Immutable
value class Damage(val value: Int) {
    init {
        require(value >= 0) { "Damage cannot be negative" }
    }
}
