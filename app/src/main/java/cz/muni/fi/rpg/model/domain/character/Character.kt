package cz.muni.fi.rpg.model.domain.character

import cz.muni.fi.rpg.model.domain.common.Money
import java.lang.IllegalArgumentException

data class Character(
    private var name: String,
    val userId: String,
    private var career: String,
    private var race: Race,
    private var stats: Stats,
    private var points: Points
) {
    private var money: Money = Money.zero()

    init {
        require(listOf(name, userId, career).all { it.isNotBlank() })
    }

    fun update(name: String, career: String, race: Race, stats: Stats, points: Points) {
        require(listOf(name, career).all { it.isNotBlank() })
        this.name = name
        this.career = career
        this.race = race
        this.stats = stats
        this.points = points
    }

    fun addMoney(amount: Money) {
        money += amount
    }

    /**
     * @throws NotEnoughMoney
     */
    fun subtractMoney(amount: Money) {
        try {
            money -= amount
        } catch (e: IllegalArgumentException) {
            throw NotEnoughMoney(amount, e)
        }
    }

    fun getName(): String = name

    fun getCareer(): String = career

    fun getRace(): Race = race

    fun getMoney() = money

    fun getPoints(): Points = points

    fun getStats(): Stats = stats

    fun updatePoints(newPoints: Points) {
        points = newPoints
    }
}
