package cz.muni.fi.rpg.model.domain.encounter

data class Wounds(
    val current: Int,
    val max: Int
) {

    init {
        require(current >= 0)
        require(max > 0)
        require(current <= max)
    }
}