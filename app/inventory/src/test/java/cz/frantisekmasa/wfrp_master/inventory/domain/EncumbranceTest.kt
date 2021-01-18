package cz.frantisekmasa.wfrp_master.inventory.domain

import org.junit.Test

import org.junit.Assert.*

class EncumbranceTest {
    @Test
    fun fromStringThrowsExceptionIfStringIsNotNumber() {
        assertThrows(IllegalArgumentException::class.java) {
            Encumbrance.fromString("a")
        }
    }

    @Test
    fun fromStringThrowsExceptionIfStringIsBlank() {
        assertThrows(IllegalArgumentException::class.java) {
            Encumbrance.fromString(" ")
        }
    }

    @Test
    fun fromStringReadsCorrectNumber() {
        assertEquals(Encumbrance(0), Encumbrance.fromString("0"))
        assertEquals(Encumbrance(0), Encumbrance.fromString("0.0"))
        assertEquals(Encumbrance(15_000), Encumbrance.fromString("15"))
        assertEquals(Encumbrance(1), Encumbrance.fromString("0.001"))
    }

    @Test
    fun toStringReturnsCorrectValue() {
        assertEquals("0", Encumbrance(0).toString())
        assertEquals("0.001", Encumbrance(1).toString())
        assertEquals("15.1", Encumbrance(15_100).toString())
        assertEquals("0.001", Encumbrance(1).toString())
        assertEquals("10", Encumbrance(10_000).toString())
        assertEquals("12", Encumbrance(12_000).toString())
    }
}