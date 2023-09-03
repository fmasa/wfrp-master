package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.trappings

import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.WeaponQuality
import kotlin.test.Test
import kotlin.test.assertEquals

class UtilsTest {

    @Test
    fun `matchEnum() parses all feature types`() {
        assertEquals(
            mapOf(
                WeaponQuality.SLASH to 3,
                WeaponQuality.DAMAGING to 1,
                WeaponQuality.SHIELD to 2,
            ),
            parseFeatures("Slash (3A), Damaging*, Shield 2, Unknown")
        )
    }
}
