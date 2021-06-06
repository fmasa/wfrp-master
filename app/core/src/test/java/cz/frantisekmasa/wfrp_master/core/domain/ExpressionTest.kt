package cz.frantisekmasa.wfrp_master.core.domain

import junit.framework.TestCase

class ExpressionTest : TestCase() {
    fun testSimpleArithmetics() {
        assertSame(1, "1".evaluate())
        assertSame(2, "1 + 1".evaluate())
        assertSame(1, "2 - 1".evaluate())
        assertSame(8, "2 * 4".evaluate())
        assertSame(4, "12 / 3".evaluate())
    }

    fun testOperatorPriority() {
        assertSame(14, "2 + 4 * 3".evaluate())
        assertSame(2, "6 - 2 * 2".evaluate())
        assertSame(6, "8 / 4 * 3".evaluate())
        assertSame(6, "8 * 3 / 4".evaluate())
    }

    fun testDiceRoll() {
        assertTrue("1d20".evaluate() in 1..20)
        assertSame(5, "5d1".evaluate())
    }

    fun testDivisionByZeroIsTreatedAsZero() {
        assertSame(0, "1 / 0".evaluate())
    }

    fun testParentheses() {
        assertSame(30, "(1 + 2) * 10".evaluate())
        assertSame(30, "10 * (1 + 2)".evaluate())
        assertSame(1, "100 / (20 * 5)".evaluate())
        assertSame(40, "20 * (10 / 5)".evaluate())
    }

    fun testInvalidExpressionThrows() {
        // Mismatched parentheses
        assertThrowsExpressionError("(1 + 1")
        assertThrowsExpressionError("1 + 1)")

        // Unknown operator
        assertThrowsExpressionError("1 ? 5")

        // Invalid dice expression
        assertThrowsExpressionError("1d0")
        assertThrowsExpressionError("1d-1")
        assertThrowsExpressionError("1dd")
    }

    private fun assertThrowsExpressionError(expression: String) {
        assertTrue(
            try {
                expression.evaluate()

                false
            } catch(e: InvalidExpression) {
                true
            }
        )
    }

    private fun String.evaluate() = Expression.fromString(this).evaluate()
}