package cz.muni.fi.rpg.model.domain.character

data class Character(
    val name: String,
    val userId: String,
    private val career: String,
    private val race: Race,
    private val stats: Stats,
    private val points: Points
) {
    init {
        require(listOf(name, userId, career).all { it.isNotEmpty() })
    }
}
