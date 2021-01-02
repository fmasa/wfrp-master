package cz.frantisekmasa.wfrp_master.combat.domain.encounter

data class Wounds(
    val current: Int,
    val max: Int
) {
    companion object {
        fun fromMax(max: Int) = Wounds(max, max)
    }

    init {
        require(current >= 0)
        require(max > 0)
        require(current <= max)
    }
}