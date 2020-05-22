package cz.muni.fi.rpg.model.domain.character

data class Character(
    val name: String,
    val userId: String,
    val career: String,
    val race: Race,
    private val stats: Stats,
    private var points: Points
) {
    init {
        require(listOf(name, userId, career).all { it.isNotBlank() })
    }

    fun getPoints(): Points = points

    fun getStats(): Stats = stats

    fun updatePoints(newPoints: Points) {
        points = newPoints
    }
}
