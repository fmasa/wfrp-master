package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.grammars

import com.github.h0tk3y.betterParse.grammar.parseToEnd
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Blessing
import kotlin.test.Test
import kotlin.test.assertEquals

class BlessingListGrammarTest {

    @Test
    fun test() {
        val data = """
            Blessing of Battle
            Range: 6 yards
            Target: 1
            Duration: 6 rounds
            Your target gains +10 WS.
            Blessing of Breath
            Range: 6 yards
            Target: 1
            Duration: 6 rounds
            Your target does not need to breathe and
            ignores rules for suffocation.
        """.trimIndent()

        val blessings = BlessingListGrammar.parseToEnd(data)

        assertEquals(
            listOf(
                Blessing(
                    blessings[0].id,
                    name = "Blessing of Battle",
                    range = "6 yards",
                    target = "1",
                    duration = "6 rounds",
                    effect = "Your target gains +10 WS."
                ),
                Blessing(
                    blessings[1].id,
                    name = "Blessing of Breath",
                    range = "6 yards",
                    target = "1",
                    duration = "6 rounds",
                    effect = """
                        Your target does not need to breathe and
                        ignores rules for suffocation.
                    """.trimIndent()
                ),
            ),
            blessings,
        )
    }
}
