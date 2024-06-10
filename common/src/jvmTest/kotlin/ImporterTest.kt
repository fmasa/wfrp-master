import cz.frantisekmasa.wfrp_master.common.compendium.domain.SpellLore
import cz.frantisekmasa.wfrp_master.common.compendium.domain.TrappingType
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.books.ArchivesOfTheEmpire1
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.books.Book
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.books.CoreRulebook
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.books.EnemyInShadowsCompanion
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.books.UpInArms
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.books.WindsOfMagic
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.Document
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.loadDocument
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.MeleeWeaponGroup
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.RangedWeaponGroup
import org.junit.Assume.assumeTrue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ImporterTest {
    @Test
    fun `careers import (Core Rulebook)`() {
        withBook(CoreRulebook) { document ->
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
        withBook(WindsOfMagic) { document ->
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
        withBook(UpInArms) { document ->
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
    fun `careers import (Archives of the Empire I)`() {
        withBook(ArchivesOfTheEmpire1) { document ->
            val careers = ArchivesOfTheEmpire1.importCareers(document)
            assertEquals(4, careers.size)
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
        withBook(CoreRulebook) { document ->
            val skills = CoreRulebook.importSkills(document)
            assertEquals(123, skills.size)
        }
    }

    @Test
    fun `talents import (Core Rulebook)`() {
        withBook(CoreRulebook) { document ->
            val talents = CoreRulebook.importTalents(document)
            assertEquals(167, talents.size)
        }
    }

    @Test
    fun `talents import (Up in Arms)`() {
        withBook(UpInArms) { document ->
            val talents = UpInArms.importTalents(document)
            assertEquals(12, talents.size)
        }
    }

    @Test
    fun `traits import (Core Rulebook)`() {
        withBook(CoreRulebook) { document ->
            val traits = CoreRulebook.importTraits(document)
            assertEquals(81, traits.size)
        }
    }

    @Test
    fun `spell import (Core Rulebook)`() {
        withBook(CoreRulebook) { document ->
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
                spells.size,
            )
        }
    }

    @Test
    fun `spell import (Winds of Magic)`() {
        withBook(WindsOfMagic) { document ->
            val spells = WindsOfMagic.importSpells(document)
            assertEquals(
                (
                    8 * SpellLore.entries.size + // Arcane Spells
                        (8 * 24) // Color spells
                ),
                spells.size,
            )
        }
    }

    @Test
    fun `spell import (Enemy in Shadows - Companion)`() {
        withBook(EnemyInShadowsCompanion) { document ->
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
        withBook(CoreRulebook) { document ->
            val blessings = CoreRulebook.importBlessings(document)

            assertEquals(19, blessings.size)
        }
    }

    @Test
    fun `miracles import (Core Rulebook)`() {
        withBook(CoreRulebook) { document ->
            val miracles = CoreRulebook.importMiracles(document)

            assertEquals(10 * 6, miracles.size)
        }
    }

    @Test
    fun `trappings import (Core Rulebook)`() {
        withBook(CoreRulebook) { document ->
            val trappings = CoreRulebook.importTrappings(document)

            assertEquals(227, trappings.size)
        }
    }

    @Test
    fun `trappings import (Up in Arms)`() {
        withBook(UpInArms) { document ->
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

    @Test
    fun `trappings import (Winds of Magic)`() {
        withBook(WindsOfMagic) { document ->
            val trappings = WindsOfMagic.importTrappings(document)

            assertEquals(
                3,
                trappings.count { it.trappingType == TrappingType.ClothingOrAccessory },
            )
        }
    }

    @Test
    fun `trappings import (Archives of the Empire I)`() {
        withBook(ArchivesOfTheEmpire1) { document ->
            val trappings = ArchivesOfTheEmpire1.importTrappings(document)

            trappings.forEach { assertTrue { it.description.isNotBlank() } }

            assertEquals(
                8,
                trappings.count { it.trappingType is TrappingType.MeleeWeapon },
            )
            assertEquals(
                6,
                trappings.count { it.trappingType is TrappingType.RangedWeapon },
            )
            assertEquals(
                4,
                trappings.count { it.trappingType is TrappingType.Ammunition },
            )
        }
    }

    private fun withBook(
        book: Book,
        block: (Document) -> Unit,
    ) {
        val pdf = ConfigProvider.getRulebookPdf(book)
        assumeTrue("PDF is found", pdf != null && pdf.exists())
        loadDocument(pdf!!.inputStream()).use(block)
    }
}
