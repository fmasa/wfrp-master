package cz.frantisekmasa.wfrp_master.core.domain.character

import org.junit.Assert.*
import org.junit.Test

class CurrentConditionsTest {
    @Test
    fun testRemovalOfMissingConditionIsNoop() {
        val conditions = CurrentConditions.none().addConditions(Condition.UNCONSCIOUS)

        assertSame(conditions, conditions.removeCondition(Condition.SURPRISED))
    }

    @Test
    fun testAddingMissingConditionSetsConditionCountToOne() {
        assertEquals(
            mapOf(Condition.FATIGUED to 1),
            CurrentConditions.none().addConditions(Condition.FATIGUED).toMap()
        )
    }

    @Test
    fun testAddingExistingConditionIncrementsCount() {
        assertEquals(
            mapOf(Condition.FATIGUED to 2),
            CurrentConditions.none()
                .addConditions(Condition.FATIGUED)
                .addConditions(Condition.FATIGUED)
                .toMap()
        )
    }

    @Test
    fun testRemovingExistingConditionWithCountLargerThanOneDecrementsCount() {
        assertEquals(
            CurrentConditions.none().addConditions(Condition.FATIGUED),
            CurrentConditions.none()
                .addConditions(Condition.FATIGUED, Condition.FATIGUED)
                .removeCondition(Condition.FATIGUED)
        )
    }

    @Test
    fun testRemovingExistingConditionWithCountOneThanOneRemovesIt() {
        assertEquals(
            CurrentConditions.none(),
            CurrentConditions.none()
                .addConditions(Condition.FATIGUED)
                .removeCondition(Condition.FATIGUED)
        )
    }

    @Test
    fun testAddingExistingNonStackableConditionIsNoop() {
        val conditions =  CurrentConditions.none().addConditions(Condition.UNCONSCIOUS)

        assertSame(conditions, conditions.addConditions(Condition.UNCONSCIOUS))
    }

    @Test
    fun testAddingNoConditionIsNoop() {
        val conditions = CurrentConditions.none()

        assertSame(conditions, conditions.addConditions())
    }
}