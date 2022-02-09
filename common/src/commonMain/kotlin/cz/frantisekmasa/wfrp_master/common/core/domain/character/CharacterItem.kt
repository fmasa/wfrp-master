package cz.frantisekmasa.wfrp_master.common.core.domain.character

import android.os.Parcelable
import java.util.UUID

interface CharacterItem : Parcelable {
    val id: UUID
    val compendiumId: UUID?
}
