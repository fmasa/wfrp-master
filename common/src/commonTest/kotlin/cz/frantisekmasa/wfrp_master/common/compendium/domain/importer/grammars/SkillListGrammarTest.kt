package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.grammars

import com.github.h0tk3y.betterParse.grammar.parseToEnd
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Skill
import cz.frantisekmasa.wfrp_master.common.core.domain.Characteristic
import kotlin.test.Test
import kotlin.test.assertEquals

class SkillListGrammarTest {

    @Test
    fun test() {
        val data = """
            Some garbage above first skill.
            Haggle (Fel) basic
            Haggle allows you to secure better deals when negotiating with
            others. In general, Haggle is used to see whether you do, or do
            not, make a good deal, most commonly with an Opposed Haggle
            Test. Specifically, it can be used when shopping to secure better
            prices. For information on this, refer to Chapter 11: Consumers’
            Guide.
            Heal (Int) advanced
            You’ve been trained to deal with injuries and diseases. A successful
            Heal Test allows you to do one of the following:
            Diagnose an illness, infection, or disease.
            Treat a disease (see page 188).
            Art (Dex) basic, grouped
            Create works of art in your chosen medium.
            Not having access to appropriate Trade Tools will incur a penalty
            to your Test.
            Specialisations: Cartography, Engraving, Wood-carving
        """.trimIndent()

        val result = SkillListGrammar.parseToEnd(data)

        assertEquals(
            listOf(
                Skill(
                    id = result[0].id,
                    name = "Haggle",
                    description = """
                        Haggle allows you to secure better deals when negotiating with
                        others. In general, Haggle is used to see whether you do, or do
                        not, make a good deal, most commonly with an Opposed Haggle
                        Test. Specifically, it can be used when shopping to secure better
                        prices. For information on this, refer to Chapter 11: Consumers’
                        Guide.
                    """.trimIndent(),
                    characteristic = Characteristic.FELLOWSHIP,
                    advanced = false,
                ),
                Skill(
                    id = result[1].id,
                    name = "Heal",
                    description = """
                        You’ve been trained to deal with injuries and diseases. A successful
                        Heal Test allows you to do one of the following:
                        Diagnose an illness, infection, or disease.
                        Treat a disease (see page 188).
                    """.trimIndent(),
                    characteristic = Characteristic.INTELLIGENCE,
                    advanced = true,
                ),
                Skill(
                    id = result[2].id,
                    name = "Art (Cartography)",
                    description = """
                        Create works of art in your chosen medium.
                        Not having access to appropriate Trade Tools will incur a penalty
                        to your Test.
                    """.trimIndent(),
                    Characteristic.DEXTERITY,
                    advanced = false,
                ),
                Skill(
                    id = result[3].id,
                    name = "Art (Engraving)",
                    description = """
                        Create works of art in your chosen medium.
                        Not having access to appropriate Trade Tools will incur a penalty
                        to your Test.
                    """.trimIndent(),
                    Characteristic.DEXTERITY,
                    advanced = false,
                ),
                Skill(
                    id = result[4].id,
                    name = "Art (Wood-carving)",
                    description = """
                        Create works of art in your chosen medium.
                        Not having access to appropriate Trade Tools will incur a penalty
                        to your Test.
                    """.trimIndent(),
                    Characteristic.DEXTERITY,
                    advanced = false,
                ),
            ),
            result,
        )
    }
}
