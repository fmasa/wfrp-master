package cz.frantisekmasa.wfrp_master.common.core.domain.character

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class CurrentConditionsTest {
    @Test
    fun `removal of missing condition is noop`() {
        val conditions = CurrentConditions.none().addConditions(Condition.UNCONSCIOUS)

        assertSame(conditions, conditions.removeCondition(Condition.SURPRISED))
    }

    @Test
    fun `adding missing condition sets condition count to one`() {
        assertEquals(
            mapOf(Condition.FATIGUED to 1),
            CurrentConditions.none().addConditions(Condition.FATIGUED).toMap()
        )
    }

    @Test
    fun `adding existing condition increments count`() {
        assertEquals(
            mapOf(Condition.FATIGUED to 2),
            CurrentConditions.none()
                .addConditions(Condition.FATIGUED)
                .addConditions(Condition.FATIGUED)
                .toMap()
        )
    }

    @Test
    fun `removing existing condition with count larger than one decrements count`() {
        assertEquals(
            CurrentConditions.none().addConditions(Condition.FATIGUED),
            CurrentConditions.none()
                .addConditions(Condition.FATIGUED, Condition.FATIGUED)
                .removeCondition(Condition.FATIGUED)
        )
    }

    @Test
    fun `removing existing condition with count one removes it`() {
        assertEquals(
            CurrentConditions.none(),
            CurrentConditions.none()
                .addConditions(Condition.FATIGUED)
                .removeCondition(Condition.FATIGUED)
        )
    }

    @Test
    fun `adding existing non-stackable condition is noop`() {
        val conditions = CurrentConditions.none().addConditions(Condition.UNCONSCIOUS)

        assertSame(conditions, conditions.addConditions(Condition.UNCONSCIOUS))
    }

    @Test
    fun testAddingNoConditionIsNoop() {
        val conditions = CurrentConditions.none()

        assertSame(conditions, conditions.addConditions())
    }
}
