package cz.frantisekmasa.wfrp_master.common.core.domain

@JvmInline
value class Yards(val value: Int) {
    init {
        require(value >= 0) { "Yards cannot be negative" }
    }
}
