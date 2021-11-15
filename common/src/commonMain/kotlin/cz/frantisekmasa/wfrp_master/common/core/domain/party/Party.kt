package cz.frantisekmasa.wfrp_master.common.core.domain.party

import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.core.auth.UserId
import cz.frantisekmasa.wfrp_master.common.core.domain.Ambitions
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.EncounterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.combat.Combat
import cz.frantisekmasa.wfrp_master.common.core.domain.party.combat.Combatant
import cz.frantisekmasa.wfrp_master.common.core.domain.party.settings.Settings
import cz.frantisekmasa.wfrp_master.common.core.domain.time.DateTime
import cz.frantisekmasa.wfrp_master.common.core.domain.time.ImperialDate
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelable
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Party(
    val id: PartyId,
    private var name: String,
    val gameMasterId: String?, // Remove support for single-player parties in 1.14
    private var users: Set<String>,
    private var archived: Boolean = false,
    private var ambitions: Ambitions = Ambitions("", ""),
    private var time: DateTime = DateTime(
        ImperialDate.of(ImperialDate.StandaloneDay.HEXENSTAG, 2512),
        DateTime.TimeOfDay(12, 0)
    ),
    private var activeCombat: Combat? = null,
    private var settings: Settings = Settings()
) : Parcelable {
    companion object {
        const val NAME_MAX_LENGTH = 50
    }

    private val accessCode = uuid4().toString()

    init {
        require(gameMasterId == null || gameMasterId in users)
        require(name.isNotBlank()) { "Party name must not be empty" }
        require(name.length <= NAME_MAX_LENGTH) { "Party name is too long" }
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

        require(activeCombat != null && combat.encounterId == activeCombat.encounterId)

        this.activeCombat = combat
    }

    fun updateSettings(settings: Settings) {
        this.settings = settings
    }

    fun endCombat() {
        activeCombat = null
    }

    fun hasActiveCombat(): Boolean {
        return activeCombat != null
    }

    fun getSettings(): Settings = settings

    fun getActiveCombat(): Combat? = activeCombat

    fun getAmbitions() = ambitions

    fun getTime() = time

    fun getInvitation() = Invitation(id, name, accessCode)

    fun changeTime(time: DateTime) {
        this.time = time
    }

    fun leave(userId: UserId) {
        users = users.filter { it != userId.toString() }.toSet()
    }

    fun isMember(userId: UserId): Boolean = users.contains(userId.toString())

    fun getPlayers(): List<UserId> =
        users.filter { it != gameMasterId }
            .map(UserId::fromString)
}
