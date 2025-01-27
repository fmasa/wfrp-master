package cz.frantisekmasa.wfrp_master.common.core.domain.rolls

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TestResultTest {
    @Test
    fun `rolling 100 is fumble`() {
        val testResult = TestResult(100, 60)
        assertTrue(testResult.isFumble)
    }

    @Test
    fun `rolling under tested value is success`() {
        val testResult = TestResult(9, 60)
        assertEquals("+6", testResult.successLevelText)
    }

    @Test
    fun `rolling exactly the value is success`() {
        val testResult = TestResult(60, 60)
        assertEquals("+0", testResult.successLevelText)
    }

    @Test
    fun `rolling bellow auto success threshold is success`() {
        assertEquals("+2", TestResult(5, 25).successLevelText)
        assertEquals("+1", TestResult(5, 6).successLevelText)
        assertEquals("+1", TestResult(5, 1).successLevelText)
    }

    @Test
    fun `rolling double under tested value is critical`() {
        val testResult = TestResult(11, 60)
        assertTrue(testResult.isCritical)
    }

    @Test
    fun `rolling over value is failure`() {
        val testResult = TestResult(16, 15)
        assertEquals("-0", testResult.successLevelText)
    }

    @Test
    fun `rolling above auto failure threshold is success`() {
        assertEquals("-2", TestResult(96, 70).successLevelText)
        assertEquals("-1", TestResult(96, 97).successLevelText)
        assertEquals("-1", TestResult(96, 95).successLevelText)
    }

    @Test
    fun `rolling double over roll value is fumble`() {
        val testResult = TestResult(77, 70)
        assertTrue(testResult.isFumble)
    }
}
