package cz.muni.fi.rpg.model.domain.character

import cz.muni.fi.rpg.model.domain.common.Ambitions
import cz.muni.fi.rpg.model.domain.common.Money
import java.lang.IllegalArgumentException

data class Character(
    private var name: String,
    val userId: String,
    private var career: String,
    private var socialClass: String,
    private var psychology: String,
    private var motivation: String,
    private var race: Race,
    private var stats: Stats,
    private var maxStats: Stats,
    private var points: Points,
    private var ambitions: Ambitions = Ambitions("", ""),
    private var mutation: String = "",
    private var note: String = ""
) {
    companion object {
        const val NAME_MAX_LENGTH = 50
        const val CAREER_MAX_LENGTH = 50
        const val SOCIAL_CLASS_MAX_LENGTH = 50
        const val PSYCHOLOGY_MAX_LENGTH = 200
        const val MOTIVATION_MAX_LENGTH = 200
        const val MUTATION_MAX_LENGTH = 200
        const val NOTE_MAX_LENGTH = 400
    }

    private var money: Money = Money.zero()

    init {
        require(listOf(name, userId, career).all { it.isNotBlank() })
        require(name.length <= NAME_MAX_LENGTH) { "Character name is too long" }
        require(career.length <= CAREER_MAX_LENGTH) { "Career is too long" }
        require(socialClass.length <= SOCIAL_CLASS_MAX_LENGTH) { "Social class is too long" }
        require(psychology.length <= PSYCHOLOGY_MAX_LENGTH) { "Psychology is too long" }
        require(motivation.length <= MOTIVATION_MAX_LENGTH) { "Motivation is too long" }
        require(mutation.length <= MUTATION_MAX_LENGTH) { "Mutation is too long" }
        require(note.length <= NOTE_MAX_LENGTH) { "Note is too long" }
    }

    fun update(
        name: String,
        career: String,
        socialClass: String,
        race: Race,
        stats: Stats,
        maxStats: Stats,
        points: Points,
        psychology: String,
        motivation: String,
        note: String
    ) {
        require(listOf(name, career).all { it.isNotBlank() })
        require(name.length <= NAME_MAX_LENGTH) { "Character name is too long" }
        require(career.length <= CAREER_MAX_LENGTH) { "Career is too long" }
        require(socialClass.length <= SOCIAL_CLASS_MAX_LENGTH) { "Social class is too long" }
        require(stats.allLowerOrEqualTo(maxStats)) { "Stats cannot be larger than max stats" }
        require(psychology.length <= PSYCHOLOGY_MAX_LENGTH) { "Psychology is too long" }
        require(motivation.length <= MOTIVATION_MAX_LENGTH) { "Motivation is too long" }
        require(note.length <= NOTE_MAX_LENGTH) { "Note is too long" }

        this.name = name
        this.career = career
        this.socialClass = socialClass
        this.race = race
        this.stats = stats
        this.maxStats = maxStats
        this.points = points
        this.psychology = psychology
        this.motivation = motivation
        this.note = note
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

    fun getSocialClass(): String = socialClass

    fun getMoney() = money

    fun updatePoints(newPoints: Points) {
        points = newPoints
    }

    fun getPoints(): Points = points

    fun getStats(): Stats = stats
    fun getMaxStats(): Stats = maxStats

    fun updateAmbitions(ambitions: Ambitions) {
        this.ambitions = ambitions
    }

    fun getAmbitions(): Ambitions = ambitions

    fun getPsychology() = psychology

    fun getMotivation() = motivation

    fun getNote() = note

    fun getMutation() = mutation
}
