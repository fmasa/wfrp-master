package cz.muni.fi.rpg.model.domain.common

data class Ambitions(
    val shortTerm: String,
    val longTerm: String
) {
    companion object {
        const val MAX_LENGTH = 400
    }

    init {
        require(shortTerm.length <= MAX_LENGTH)
        require(longTerm.length <= MAX_LENGTH)
    }
}