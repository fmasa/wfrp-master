package cz.muni.fi.rpg.model.domain.character

data class Points(
    private val insanity: Int,
    private val fate: Int,
    private val fortune: Int
) {
    init {
        require(insanity >= 0)
        require(fate >= 0)
        require(fortune in 0..fate)
    }
}