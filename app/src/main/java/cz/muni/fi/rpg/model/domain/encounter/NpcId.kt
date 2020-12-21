package cz.muni.fi.rpg.model.domain.encounter

import android.os.Parcelable
import cz.muni.fi.rpg.model.domain.encounters.EncounterId
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class NpcId(val encounterId: EncounterId, val npcId: UUID) : Parcelable