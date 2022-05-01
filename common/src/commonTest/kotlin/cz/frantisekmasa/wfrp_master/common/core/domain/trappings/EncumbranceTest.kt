package cz.frantisekmasa.wfrp_master.common.core.domain.trappings

import cz.frantisekmasa.wfrp_master.common.core.domain.Stats
import kotlin.test.Test
import kotlin.test.assertEquals

class EncumbranceTest {
    @Test
    fun maximumForCharacter() {
        assertEquals(
            "8",
            Encumbrance.maximumForCharacter(
                Stats(
                    weaponSkill = 10,
                    dexterity = 10,
                    ballisticSkill = 10,
                    strength = 64,
                    toughness = 24,
                    agility = 10,
                    intelligence = 20,
                    initiative = 45,
                    fellowship = 20,
                    willPower = 15,
                )
            ).toString()
        )
    }

    @Test
    fun toStringReturnsCorrectValue() {
        assertEquals("0.001", Encumbrance(0.0009).toString())
        assertEquals("0.001", Encumbrance(0.001).toString())
        assertEquals("15.1", Encumbrance(15.100).toString())
        assertEquals("0.001", Encumbrance(0.001).toString())
        assertEquals("10", Encumbrance(10.000).toString())
        assertEquals("12", Encumbrance(12.0).toString())
        assertEquals("12,000", Encumbrance(12000.0).toString())
    }
}
