package cz.frantisekmasa.wfrp_master.common.core.utils

import kotlin.test.Test
import kotlin.test.assertEquals

class MoveItemTest {

    @Test
    fun `sourceIndex is before targetIndex`() {
        val data = listOf(1, 2, 3, 4, 5)

        assertEquals(
            listOf(1, 4, 2, 3, 5),
            data.moveItem(3, 1),
        )
    }

    @Test
    fun `sourceIndex is after targetIndex`() {
        val data = listOf(1, 2, 3, 4, 5)

        assertEquals(
            listOf(1, 3, 4, 2, 5),
            data.moveItem(1, 3),
        )
    }
}
