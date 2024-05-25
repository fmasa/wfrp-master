package cz.frantisekmasa.wfrp_master.common.core.domain.character

import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.core.auth.UserId
import cz.frantisekmasa.wfrp_master.common.core.domain.Money
import cz.frantisekmasa.wfrp_master.common.core.domain.Stats
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class CharacterTest {
    private fun character() =
        Character(
            id = uuid4().toString(),
            name = "Bilbo",
            userId = UserId("123"),
            career = "Writer",
            socialClass = "Noble",
            status = SocialStatus(SocialStatus.Tier.GOLD, 2),
            psychology = "Does not like orcs",
            motivation = "Food",
            race = Race.HALFLING,
            characteristicsBase = Stats(20, 40, 2, 4, 80, 5, 5, 4, 0, 10),
            characteristicsAdvances = Stats(20, 40, 2, 4, 80, 5, 5, 4, 0, 10),
            points = Points(0, 4, 4, 5, 5, 0, 0, 0, 0, 0),
        )

    @Test
    fun `throws exception when trying to subtract more money than character has`() {
        val character =
            character()
                .addMoney(Money.pennies(15))

        assertFailsWith(NotEnoughMoney::class) {
            character.subtractMoney(Money.pennies(16))
        }
    }

    @Test
    fun `money are added to current balance`() {
        val character = character()
        val startingMoney = character.money

        assertEquals(
            startingMoney + Money.crowns(1) + Money.shillings(10),
            character
                .addMoney(Money.crowns(1))
                .addMoney(Money.shillings(10))
                .money,
        )
    }

    @Test
    fun `NPC with public name can be converted to PC`() {
        val character =
            Character(
                id = uuid4().toString(),
                name = "Bilbo",
                publicName = "The hobbit",
                type = CharacterType.NPC,
                userId = null,
                career = "Writer",
                socialClass = "Noble",
                psychology = "Does not like orcs",
                motivation = "Food",
                race = Race.HALFLING,
                characteristicsBase = Stats(20, 40, 2, 4, 80, 5, 5, 4, 0, 10),
                characteristicsAdvances = Stats(20, 40, 2, 4, 80, 5, 5, 4, 0, 10),
                points = Points(0, 4, 4, 5, 5, 0, 0, 0, 0, 0),
            )

        val playerCharacter = character.turnIntoPlayerCharacter()

        assertEquals(CharacterType.PLAYER_CHARACTER, playerCharacter.type)
        assertNull(playerCharacter.publicName)
    }
}
