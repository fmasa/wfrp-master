package cz.muni.fi.rpg.model.domain.character

data class Points(
   val insanity: Int,
   val fate: Int,
   val fortune: Int,
   val wounds: Int
) {
    init {
        require(insanity >= 0)
        require(fate >= 0)
        require(fortune in 0..fate)
        require(wounds >= 0)
    }
}