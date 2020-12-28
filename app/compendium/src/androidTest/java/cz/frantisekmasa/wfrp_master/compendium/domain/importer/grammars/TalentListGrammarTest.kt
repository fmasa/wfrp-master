package cz.frantisekmasa.wfrp_master.compendium.domain.importer.grammars

import com.github.h0tk3y.betterParse.grammar.parseToEnd
import cz.frantisekmasa.wfrp_master.compendium.domain.Talent
import junit.framework.TestCase

class TalentListGrammarTest : TestCase() {
    fun test() {
        val data = """
            Some garbage above first talent.
            Talent1
            Max: 2
            Some.
            Description. Ok?
            Talent2
            Max: Your WPB
            This is yet another description.
        """.trimIndent()

        val result = TalentListGrammar.parseToEnd(data)

        assertEquals(
            listOf(
                Talent(
                    id = result[0].id,
                    name = "Talent1",
                    maxTimesTaken = "2",
                    description = """
                        Some.
                        Description. Ok?
                    """.trimIndent(),
                ),
                Talent(
                    id = result[1].id,
                    name = "Talent2",
                    maxTimesTaken = "Your WPB",
                    description = "This is yet another description."
                ),
            ),
            result,
        )
    }
}