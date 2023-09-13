package cz.frantisekmasa.wfrp_master.common.compendium

import com.benasher44.uuid.Uuid

interface CompendiumListItem {
    val id: Uuid
    val name: String
}
