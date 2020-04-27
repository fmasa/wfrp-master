package cz.muni.fi.rpg.model.domain.character

data class Character(
    val name: String,
    val userId: String,
    val career: String,
    val race: Race,
    val stats: Stats,
    var points: Points
) {
    init {
        require(listOf(name, userId, career).all { it.isNotEmpty() })
    }

    fun updatePoints(newPoints: Points) {
        points = newPoints
    }
}
