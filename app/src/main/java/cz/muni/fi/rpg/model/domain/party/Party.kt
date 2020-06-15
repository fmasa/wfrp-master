package cz.muni.fi.rpg.model.domain.party
import cz.muni.fi.rpg.model.domain.common.Ambitions
import cz.muni.fi.rpg.model.generateAccessCode
import java.util.*

data class Party(
    val id: UUID,
    val name: String,
    val gameMasterId: String?,
    val users: Set<String>,
    private var ambitions: Ambitions = Ambitions("", "")
) {
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

    private val accessCode = generateAccessCode();

    init {
        require(id.version() == 4) {"Party identifier must be UUIDv4"}
        require(name.isNotBlank()) {"Party name must not be empty"}
        require(name.length <= NAME_MAX_LENGTH) {"Party name is too long"}
    }

    fun isSinglePlayer() = gameMasterId == null

    fun updateAmbitions(ambitions: Ambitions) {
        this.ambitions = ambitions
    }

    fun getAmbitions() = ambitions

    fun getInvitation() = Invitation(id, name, accessCode)
}
