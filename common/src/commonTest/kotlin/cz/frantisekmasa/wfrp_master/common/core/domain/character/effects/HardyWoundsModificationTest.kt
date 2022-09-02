package cz.frantisekmasa.wfrp_master.common.core.domain.character.effects

import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.character.effects.HardyWoundsModification
import cz.frantisekmasa.wfrp_master.common.core.domain.Stats
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterType
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Points
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Race
import cz.frantisekmasa.wfrp_master.common.core.domain.character.SocialStatus
import kotlin.test.Test
import kotlin.test.assertEquals

class HardyWoundsModificationTest {

    @Test
    fun `applying modification adds current TB to max Wounds`() {
        val character = character()
        assertEquals(6, character.wounds.max)

        val updatedCharacter = HardyWoundsModification(1).apply(character, emptyList())

        assertEquals(8, updatedCharacter.wounds.max)
    }

    @Test
    fun `applying modification adds current TB multiple times to max Wounds when Hardy is taken multiple times`() {
        val character = character()
        assertEquals(6, character.wounds.max)

        val updatedCharacter = HardyWoundsModification(2).apply(character, emptyList())

        assertEquals(10, updatedCharacter.wounds.max)
    }

    @Test
    fun `reverting more Hardy bonuses than Character has goes to zero`() {
        val character = HardyWoundsModification(2).apply(character(), emptyList())

        val updatedCharacter = HardyWoundsModification(3).revert(character, emptyList())

        assertEquals(6, updatedCharacter.wounds.max)
    }

    @Test
    fun `reverting Hardy bonus subtracts correct amount of max Wounds`() {
        val character = HardyWoundsModification(3).apply(character(), emptyList())

        val updatedCharacter = HardyWoundsModification(2).revert(character, emptyList())

        assertEquals(8, updatedCharacter.wounds.max)
    }

    private fun character(): Character {
        return Character(
            id = uuid4().toString(),
            type = CharacterType.NPC,
            name = "Bjorn Kveldulfson",
            userId = null,
            career = "Watchman",
            status = SocialStatus(SocialStatus.Tier.BRASS, 1),
            psychology = "",
            motivation = "",
            characteristicsBase = Stats(
                10,
                10,
                10,
                10,
                20,
                10,
                10,
                10,
                10,
                10,
            ),
            characteristicsAdvances = Stats.ZERO,
            points = Points(
                corruption = 0,
                fate = 0,
                fortune = 0,
                maxWounds = null,
                wounds = 0,
                resilience = 0,
                resolve = 0,
                sin = 0,
                experience = 0,
                spentExperience = 0,
                hardyWoundsBonus = 0,
            ),
            socialClass = "Warriors",
            race = Race.HUMAN,
        )
    }
}