package cz.muni.fi.rpg.model.domain.character

import cz.muni.fi.rpg.model.domain.common.Money
import java.lang.IllegalArgumentException

data class Character(
    val name: String,
    val userId: String,
    val career: String,
    val race: Race,
    private val stats: Stats,
    private var points: Points
) {
    private var money: Money = Money.zero()

    init {
        require(listOf(name, userId, career).all { it.isNotBlank() })
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

    fun getMoney() = money

    fun getPoints(): Points = points

    fun getStats(): Stats = stats

    fun updatePoints(newPoints: Points) {
        points = newPoints
    }
}
