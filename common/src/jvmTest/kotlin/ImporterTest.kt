import cz.frantisekmasa.wfrp_master.common.compendium.domain.SpellLore
import cz.frantisekmasa.wfrp_master.common.compendium.domain.TrappingType
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.books.CoreRulebook
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.books.EnemyInShadowsCompanion
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.books.UpInArms
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.books.WindsOfMagic
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.Document
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.loadDocument
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.MeleeWeaponGroup
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.RangedWeaponGroup
import java.io.InputStream
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

/@Ignore
class ImporterTest {

    @Test
    fun `careers import (Core Rulebook)`() {
        withCoreRuleBook { document ->
            val careers = CoreRulebook.importCareers(document)
            assertEquals(64, careers.size)
            careers.forEach {
                assertEquals(3, it.levels[0].characteristics.size)
                assertEquals(1, it.levels[1].characteristics.size)
                assertEquals(1, it.levels[2].characteristics.size)
                assertEquals(1, it.levels[3].characteristics.size)
            }
        }
    }

    @Test
    fun `careers import (Winds of Magic)`() {
        withWindsOfMagic { document ->
            val careers = WindsOfMagic.importCareers(document)

            assertEquals(11, careers.size)
            careers.forEach {
                assertEquals(3, it.levels[0].characteristics.size)
                assertEquals(1, it.levels[1].characteristics.size)
                assertEquals(1, it.levels[2].characteristics.size)
                assertEquals(1, it.levels[3].characteristics.size)
            }
        }
    }

    @Test
    fun `careers import (Up in Arms)`() {
        withUpInArms { document ->
            val careers = UpInArms.importCareers(document)
            assertEquals(14, careers.size)
            careers.forEach {
                assertEquals(3, it.levels[0].characteristics.size)
                assertEquals(1, it.levels[1].characteristics.size)
                assertEquals(1, it.levels[2].characteristics.size)
                assertEquals(1, it.levels[3].characteristics.size)
            }
        }
    }

    @Test
    fun `skills import (Core Rulebook)`() {
        withCoreRuleBook { document ->
            val skills = CoreRulebook.importSkills(document)
            assertEquals(123, skills.size)
        }
    }

    @Test
    fun `talents import (Core Rulebook)`() {
        withCoreRuleBook { document ->
            val talents = CoreRulebook.importTalents(document)
            assertEquals(167, talents.size)
        }
    }

    @Test
    fun `talents import (Up in Arms)`() {
        withUpInArms { document ->
            val talents = UpInArms.importTalents(document)
            assertEquals(12, talents.size)
        }
    }

    @Test
    fun `traits import (Core Rulebook)`() {
        withCoreRuleBook { document ->
            val traits = CoreRulebook.importTraits(document)
            assertEquals(81, traits.size)
        }
    }

    @Test
    fun `spell import (Core Rulebook)`() {
        withCoreRuleBook { document ->
            val spells = CoreRulebook.importSpells(document)
            assertEquals(
                (
                    23 * SpellLore.values().size + // Arcane Spells
                        25 + // Petty Spells
                        8 * 8 + // Color Spells
                        2 + // Hedgecraft Spells
                        10 + // Witchcraft Spells
                        4 + // Daemonology Spells
                        4 + // Necromancy Spells
                        3 // Chaos Spells
                    ),
                spells.size
            )
        }
    }

    @Test
    fun `spell import (Winds of Magic)`() {
        withWindsOfMagic { document ->
            val spells = WindsOfMagic.importSpells(document)
            assertEquals(
                (
                    8 * SpellLore.values().size + // Arcane Spells
                        (8 * 24) // Color spells
                    ),
                spells.size
            )
        }
    }

    @Test
    fun `spell import (Enemy in Shadows - Companion)`() {
        withEnemyInShadowsCompanion { document ->
            val spells = EnemyInShadowsCompanion.importSpells(document)
            assertEquals(
                (
                    9 * 4 + // Chaos Arcane Spells
                        14 // Tzeentch Spells
                    ),
                spells.size,
            )
        }
    }

    @Test
    fun `blessings import (Core Rulebook)`() {
        withCoreRuleBook { document ->
            val blessings = CoreRulebook.importBlessings(document)

            assertEquals(19, blessings.size)
        }
    }

    @Test
    fun `miracles import (Core Rulebook)`() {
        withCoreRuleBook { document ->
            val miracles = CoreRulebook.importMiracles(document)

            assertEquals(10 * 6, miracles.size)
        }
    }

    @Test
    fun `trappings import (Core Rulebook)`() {
        withCoreRuleBook { document ->
            val trappings = CoreRulebook.importTrappings(document)

            assertEquals(227, trappings.size)
        }
    }

    @Test
    fun `trappings import (Up in Arms)`() {
        withUpInArms { document ->
            val trappings = UpInArms.importTrappings(document)
            val countMeleeWeapons = { group: MeleeWeaponGroup ->
                trappings.count { (it.trappingType as? TrappingType.MeleeWeapon)?.group == group }
            }
            val countRangedWeapons = { group: RangedWeaponGroup ->
                trappings.count { (it.trappingType as? TrappingType.RangedWeapon)?.group == group }
            }

            assertEquals(12, trappings.count { it.trappingType == null })
            assertEquals(11 + 4, countMeleeWeapons(MeleeWeaponGroup.BASIC))
            assertEquals(4, countMeleeWeapons(MeleeWeaponGroup.CAVALRY))
            assertEquals(3, countMeleeWeapons(MeleeWeaponGroup.FENCING))
            assertEquals(7, countMeleeWeapons(MeleeWeaponGroup.BRAWLING))
            assertEquals(7, countMeleeWeapons(MeleeWeaponGroup.BRAWLING))
            assertEquals(3, countMeleeWeapons(MeleeWeaponGroup.FLAIL))
            assertEquals(4, countMeleeWeapons(MeleeWeaponGroup.PARRY))
            assertEquals(9, countMeleeWeapons(MeleeWeaponGroup.POLEARM))
            assertEquals(6, countMeleeWeapons(MeleeWeaponGroup.TWO_HANDED))
            assertEquals(9 + 11, trappings.count { it.trappingType is TrappingType.Ammunition })
            assertEquals(11, countRangedWeapons(RangedWeaponGroup.BLACKPOWDER))
            assertEquals(5, countRangedWeapons(RangedWeaponGroup.ENGINEERING))
        }
    }

    private fun withCoreRuleBook(block: (Document) -> Unit) {
        loadDocument(javaClass.getResourceAsStream("rulebook.pdf") as InputStream)
            .use(block)
    }

    private fun withWindsOfMagic(block: (Document) -> Unit) {
        loadDocument(javaClass.getResourceAsStream("winds_of_magic.pdf") as InputStream)
            .use(block)
    }

    private fun withEnemyInShadowsCompanion(block: (Document) -> Unit) {
        loadDocument(
            javaClass.getResourceAsStream("enemy_in_shadows_companion.pdf") as InputStream
        ).use(block)
    }

    private fun withUpInArms(block: (Document) -> Unit) {
        loadDocument(javaClass.getResourceAsStream("up_in_arms.pdf") as InputStream)
            .use(block)
    }
}
