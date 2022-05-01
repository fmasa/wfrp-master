package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.grammars

import com.github.h0tk3y.betterParse.grammar.parseToEnd
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Spell
import kotlin.test.Test
import kotlin.test.assertEquals

class SpellListGrammarTest {

    @Test
    fun test() {
        val data = """
            [Lore of Something awesome]
            For Elves, this is just a part of growing up, and those with interest
            in magic are schooled to develop their burgeoning talents.
            Garbage. Another Garbage. Skip this Garbage.
            Push ’em
            CN: 6
            Range: You
            Target: You
            Duration: Instant
            All living creatures within Willpower Bonus yards are pushed 
            back your Willpower Bonus in yards and gain the Prone 
            Condition. If this brings them into contact with a wall or other 
            large obstacle, they take Damage equal to the distance travelled 
            in yards. For every +2 SL, you may push creatures back another 
            Willpower Bonus in yards
            Teleport
            CN: 5
            Range: You
            Target: You
            Duration: Instant
            Using magic, you can teleport up to your Willpower Bonus in 
            yards. This movement allows you to traverse gaps, avoid perils and 
            pitfalls, and ignore obstacles. For every +2 SL you may increase 
            the distance travelled by your Willpower Bonus in yards.
            Terrifying
            CN: 7
            Range: You
            Target: You
            Duration: Willpower Bonus Rounds
            Y ou gain the Terror (1) Creature Trait (see page 191).
            Ward
            CN: 5
            Range: You
            Target: You
            Duration: Willpower Bonus Rounds
            You wrap yourself in protective magic, gaining the Ward (9+) 
            Creature Trait (see page 343). 
        """.trimIndent()

        val result = SpellListGrammar("Chaos").parseToEnd(data)

        assertEquals(
            listOf(
                Spell(
                    id = result[0].id,
                    name = "Push ’em",
                    castingNumber = 6,
                    range = "You",
                    target = "You",
                    duration = "Instant",
                    effect = """
                        All living creatures within Willpower Bonus yards are pushed 
                        back your Willpower Bonus in yards and gain the Prone 
                        Condition. If this brings them into contact with a wall or other 
                        large obstacle, they take Damage equal to the distance travelled 
                        in yards. For every +2 SL, you may push creatures back another 
                        Willpower Bonus in yards
                    """.trimIndent(),
                    lore = "Chaos"
                ),
                Spell(
                    id = result[1].id,
                    name = "Teleport",
                    castingNumber = 5,
                    range = "You",
                    target = "You",
                    duration = "Instant",
                    effect = """
                        Using magic, you can teleport up to your Willpower Bonus in 
                        yards. This movement allows you to traverse gaps, avoid perils and 
                        pitfalls, and ignore obstacles. For every +2 SL you may increase 
                        the distance travelled by your Willpower Bonus in yards.
                    """.trimIndent(),
                    lore = "Chaos"
                ),
                Spell(
                    id = result[2].id,
                    name = "Terrifying",
                    castingNumber = 7,
                    range = "You",
                    target = "You",
                    duration = "Willpower Bonus Rounds",
                    effect = "Y ou gain the Terror (1) Creature Trait (see page 191).",
                    lore = "Chaos"
                ),
                Spell(
                    id = result[3].id,
                    name = "Ward",
                    castingNumber = 5,
                    range = "You",
                    target = "You",
                    duration = "Willpower Bonus Rounds",
                    effect = """
                        You wrap yourself in protective magic, gaining the Ward (9+) 
                        Creature Trait (see page 343).
                    """.trimIndent(),
                    lore = "Chaos"
                ),
            ),
            result,
        )
    }
}
