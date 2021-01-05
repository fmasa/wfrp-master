package cz.frantisekmasa.wfrp_master.compendium.domain

import android.os.Parcelable
import java.util.UUID

interface CompendiumItem : Parcelable {
    val id: UUID
}