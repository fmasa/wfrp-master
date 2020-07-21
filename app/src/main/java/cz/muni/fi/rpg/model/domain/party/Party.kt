package cz.muni.fi.rpg.model.domain.party
import android.os.Parcelable
import cz.muni.fi.rpg.model.domain.common.Ambitions
import cz.muni.fi.rpg.model.generateAccessCode
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Party(
    val id: UUID,
    private var name: String,
    val gameMasterId: String?,
    val users: Set<String>,
    private var archived: Boolean = false,
    private var ambitions: Ambitions = Ambitions("", "")
) : Parcelable {
    companion object {
        const val NAME_MAX_LENGTH = 50

        fun multiPlayerParty(id: UUID, name: String, gameMasterId: String) = Party(
            id = id,
            name = name,
            gameMasterId = gameMasterId,
            users = setOf(gameMasterId)
        )

        fun singlePlayerParty(id: UUID, name: String, playingUserId: String) = Party(
            id = id,
            name = name,
            gameMasterId = null,
            users = setOf(playingUserId)
        )
    }

    private val accessCode = generateAccessCode()

    init {
        require(id.version() == 4) {"Party identifier must be UUIDv4"}
        require(name.isNotBlank()) {"Party name must not be empty"}
        require(name.length <= NAME_MAX_LENGTH) {"Party name is too long"}
    }

    fun getPlayerCounts(): Int = users.size - 1

    fun isSinglePlayer() = gameMasterId == null

    fun updateAmbitions(ambitions: Ambitions) {
        this.ambitions = ambitions
    }

    fun rename(name: String) {
        this.name = name
    }

    fun getName(): String {
        return name;
    }

    fun archive() {
        archived = true
    }

    fun isArchived() = archived

    fun getAmbitions() = ambitions

    fun getInvitation() = Invitation(id, name, accessCode)
}
