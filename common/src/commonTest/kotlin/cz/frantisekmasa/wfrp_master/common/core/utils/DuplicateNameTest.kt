package cz.frantisekmasa.wfrp_master.common.core.utils

import kotlin.test.Test
import kotlin.test.assertEquals

class DuplicateNameTest {
    @Test
    fun `appends (Copy) to the name`() {
        assertEquals(
            "Name (Copy)",
            duplicateName("Name", Int.MAX_VALUE),
        )
    }

    @Test
    fun `shortens original name it's too long`() {
        assertEquals(
            "Na… (Copy)",
            duplicateName("Name", 10),
        )
    }

    @Test
    fun `does not shorten original if it's not necessary`() {
        assertEquals(
            "Name (Copy)",
            duplicateName("Name", 11),
        )
    }
}
