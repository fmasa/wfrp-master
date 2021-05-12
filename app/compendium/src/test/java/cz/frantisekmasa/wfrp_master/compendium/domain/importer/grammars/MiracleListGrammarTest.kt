package cz.frantisekmasa.wfrp_master.compendium.domain.importer.grammars

import cz.frantisekmasa.wfrp_master.compendium.domain.Miracle
import junit.framework.TestCase

class MiracleListGrammarTest : TestCase() {

    fun test() {
        val data = """
            Beacon of Righteous Virtue
            Range: You
            Target: Area of Effect
            Duration: Fellowship Bonus Rounds
            As you bellow prayers in Sigmar’s name, you become infused
            with holy fire of righteousness. All allies with Line of Sight to
            you instantaneously remove all Broken Conditions, and gain the
            Fearless Talent while the Miracle is in effect and they remain in
            your Line of Sight. Any Greenskins with Line of Sight to you are
            subject to Fear 1.
            Heed Not the Witch
            Range: You
            Target: Area of Effect
            Duration: Fellowship Bonus Rounds
            You call on Sigmar to protect those close to you from the fell
            influence of Chaos. Any spells that target anyone or anywhere
            within Fellowship Bonus yards suffer a penalty of –20 to Language
            (Magick) Tests, in addition to any other penalties. For every +2
            SL, you may increase the area of effect by your Fellowship Bonus
            in yards.
        """.trimIndent()

        val miracles = MiracleListGrammar("Sigmar").parseToEnd(data)

        assertEquals(
            listOf(
                Miracle(
                    miracles[0].id,
                    name = "Beacon of Righteous Virtue",
                    range = "You",
                    target = "Area of Effect",
                    duration = "Fellowship Bonus Rounds",
                    effect = """
                        As you bellow prayers in Sigmar’s name, you become infused
                        with holy fire of righteousness. All allies with Line of Sight to
                        you instantaneously remove all Broken Conditions, and gain the
                        Fearless Talent while the Miracle is in effect and they remain in
                        your Line of Sight. Any Greenskins with Line of Sight to you are
                        subject to Fear 1.
                    """.trimIndent(),
                    "Sigmar",
                ),
                Miracle(
                    miracles[1].id,
                    name = "Heed Not the Witch",
                    range = "You",
                    target = "Area of Effect",
                    duration = "Fellowship Bonus Rounds",
                    effect = """
                        You call on Sigmar to protect those close to you from the fell
                        influence of Chaos. Any spells that target anyone or anywhere
                        within Fellowship Bonus yards suffer a penalty of –20 to Language
                        (Magick) Tests, in addition to any other penalties. For every +2
                        SL, you may increase the area of effect by your Fellowship Bonus
                        in yards.
                    """.trimIndent(),
                    "Sigmar",
                ),
            ),
            miracles,
        )
    }
}