package cz.muni.fi.rpg.model.domain.character

enum class Condition {
    ABLAZE,
    BLEEDING,
    BLINDED,
    BROKEN,
    DEAFENED,
    ENTANGLED,
    FATIGUED,
    POISONED,
    PRONE,
    STUNNED,
    SURPRISED,
    UNCONSCIOUS;

    fun getFutureConditions(): Array<Condition> = when (this) {
        BLEEDING -> arrayOf(FATIGUED)
        BROKEN -> arrayOf(FATIGUED)
        POISONED -> arrayOf(FATIGUED)
        STUNNED -> arrayOf(FATIGUED)
        UNCONSCIOUS -> arrayOf(PRONE, FATIGUED)
        else -> emptyArray()
    }

    fun isStackable() = ! arrayOf(PRONE, SURPRISED, UNCONSCIOUS).contains(this)
}