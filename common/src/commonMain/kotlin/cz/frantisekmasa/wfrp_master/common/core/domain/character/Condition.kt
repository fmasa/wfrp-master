package cz.frantisekmasa.wfrp_master.common.core.domain.character

import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import dev.icerock.moko.resources.StringResource

enum class Condition(
    override val translatableName: StringResource,
) : NamedEnum {
    ABLAZE(Str.conditions_ablaze),
    BLEEDING(Str.conditions_bleeding),
    BLINDED(Str.conditions_blinded),
    BROKEN(Str.conditions_broken),
    DEAFENED(Str.conditions_deafened),
    ENTANGLED(Str.conditions_entangled),
    FATIGUED(Str.conditions_fatigued),
    POISONED(Str.conditions_poisoned),
    PRONE(Str.conditions_prone),
    STUNNED(Str.conditions_stunned),
    SURPRISED(Str.conditions_surprised),
    UNCONSCIOUS(Str.conditions_unconscious);

    @Suppress("unused") // TODO: This may be used in future
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
