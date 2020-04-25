package cz.muni.fi.rpg.model.domain.character

data class Character(
    val name: String,
    val userId: String,
    private val career: String,
    private val race: Race,
    private val stats: Stats,
    var points: Points
) {
    init {
        require(listOf(name, userId, career).all { it.isNotEmpty() })
    }

    fun updatePoints(newPoints: Points) {
        points = newPoints
    }
}
