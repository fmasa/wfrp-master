package cz.frantisekmasa.wfrp_master.common.core.domain.character

import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelable

interface CharacterItem : Parcelable {
    val id: Uuid
    val compendiumId: Uuid?
}
