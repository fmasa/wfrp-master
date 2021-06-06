package cz.frantisekmasa.wfrp_master.core.domain.compendium

import android.os.Parcelable
import java.util.*

interface CompendiumItem : Parcelable {
    val id: UUID
    val name: String
}