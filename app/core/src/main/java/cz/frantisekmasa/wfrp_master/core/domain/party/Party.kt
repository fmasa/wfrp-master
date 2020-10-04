package cz.frantisekmasa.wfrp_master.core.domain.party

import android.os.Parcelable
import cz.frantisekmasa.wfrp_master.core.domain.Ambitions
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.EncounterId
import cz.frantisekmasa.wfrp_master.core.domain.party.combat.Combat
import cz.frantisekmasa.wfrp_master.core.domain.party.combat.Combatant
import cz.frantisekmasa.wfrp_master.core.domain.time.DateTime
import cz.frantisekmasa.wfrp_master.core.domain.time.ImperialDate
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Party(
    val id: UUID,
    private var name: String,
    val gameMasterId: String?, // Remove support for single-player parties in 1.14
    val users: Set<String>,
    private var archived: Boolean = false,
    private var ambitions: Ambitions = Ambitions("", ""),
    private var time: DateTime = DateTime(
        ImperialDate.of(ImperialDate.StandaloneDay.HEXENSTAG, 2512),
        DateTime.TimeOfDay(12, 0)
    ),
    private var activeCombat: Combat? = null,
) : Parcelable {
    companion object {
        const val NAME_MAX_LENGTH = 50
    }

    private val accessCode = UUID.randomUUID().toString()

    init {
        require(gameMasterId == null || gameMasterId in users)
        require(id.version() == 4) {"Party identifier must be UUIDv4"}
        require(name.isNotBlank()) {"Party name must not be empty"}
        require(name.length <= NAME_MAX_LENGTH) {"Party name is too long"}
    }

    fun getPlayerCounts(): Int = users.size - 1

    fun updateAmbitions(ambitions: Ambitions) {
        this.ambitions = ambitions
    }

    fun rename(name: String) {
        this.name = name
    }

    fun getName(): String {
        return name
    }

    fun archive() {
        archived = true
    }

    fun startCombat(encounterId: EncounterId, combatants: List<Combatant>) {
        activeCombat = Combat(encounterId.encounterId, combatants)
    }

    fun updateCombat(combat: Combat) {
        val activeCombat = activeCombat

        require(activeCombat != null && combat.getEncounterId() == activeCombat.getEncounterId())

        this.activeCombat = combat
    }

    fun endCombat() {
        activeCombat = null
    }

    fun hasActiveCombat(): Boolean {
        return activeCombat != null
    }

    fun getActiveCombat(): Combat? = activeCombat

    fun getAmbitions() = ambitions

    fun getTime() = time

    fun getInvitation() = Invitation(id, name, accessCode)

    fun changeTime(time: DateTime) {
        this.time = time
    }
}
