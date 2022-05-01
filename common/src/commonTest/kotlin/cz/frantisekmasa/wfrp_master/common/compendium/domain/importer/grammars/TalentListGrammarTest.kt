package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.grammars

import com.github.h0tk3y.betterParse.grammar.parseToEnd
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Talent
import kotlin.test.Test
import kotlin.test.assertEquals

class TalentListGrammarTest {

    @Test
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
