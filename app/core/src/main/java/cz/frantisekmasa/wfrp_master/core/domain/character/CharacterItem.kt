package cz.frantisekmasa.wfrp_master.core.domain.character

import android.os.Parcelable
import java.util.*

interface CharacterItem : Parcelable {
    val id: UUID
    val compendiumId: UUID?
}