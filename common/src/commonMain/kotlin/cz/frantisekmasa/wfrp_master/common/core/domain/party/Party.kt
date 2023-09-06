package cz.frantisekmasa.wfrp_master.common.core.domain.party

import androidx.compose.runtime.Immutable
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.core.auth.UserId
import cz.frantisekmasa.wfrp_master.common.core.domain.Ambitions
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.EncounterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.combat.Combat
import cz.frantisekmasa.wfrp_master.common.core.domain.party.combat.Combatant
import cz.frantisekmasa.wfrp_master.common.core.domain.party.settings.Settings
import cz.frantisekmasa.wfrp_master.common.core.domain.time.DateTime
import cz.frantisekmasa.wfrp_master.common.core.domain.time.ImperialDate
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
@Immutable
data class Party(
    val id: PartyId,
    val name: String,
    val gameMasterId: UserId?, // Remove support for single-player parties in 1.14
    private val users: Set<UserId>,
    private val archived: Boolean = false,
    val ambitions: Ambitions = Ambitions("", ""),
    val time: DateTime = DateTime(
        ImperialDate.of(ImperialDate.StandaloneDay.HEXENSTAG, 2512),
        DateTime.TimeOfDay(12, 0)
    ),
    val activeCombat: Combat? = null,
    val settings: Settings = Settings(),
    private val accessCode: String = uuid4().toString()
) : Parcelable {
    companion object {
        const val NAME_MAX_LENGTH = 50
    }

    init {
        require(gameMasterId == null || gameMasterId in users)
        require(name.isNotBlank()) { "Party name must not be empty" }
        require(name.length <= NAME_MAX_LENGTH) { "Party name is too long" }
    }

    val playersCount: Int get() = users.size - 1

    fun updateAmbitions(ambitions: Ambitions) = copy(ambitions = ambitions)

    fun rename(name: String) = copy(name = name)

    fun archive() = copy(archived = true)

    fun startCombat(encounterId: EncounterId, combatants: List<Combatant>) = copy(
        activeCombat = Combat(encounterId.encounterId, combatants)
    )

    fun updateCombat(combat: Combat): Party {
        val activeCombat = activeCombat

        require(activeCombat != null && combat.encounterId == activeCombat.encounterId)

        return copy(activeCombat = combat)
    }

    fun updateSettings(settings: Settings) = copy(settings = settings)

    fun endCombat() = copy(activeCombat = null)

    fun getInvitation() = Invitation(id, name, accessCode)

    fun changeTime(time: DateTime) = copy(time = time)

    fun leave(userId: UserId) = copy(
        users = users.filter { it != userId }.toSet()
    )

    fun isMember(userId: UserId): Boolean = userId in users
}
