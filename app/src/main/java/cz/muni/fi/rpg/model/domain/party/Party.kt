package cz.muni.fi.rpg.model.domain.party
import cz.muni.fi.rpg.model.generateAccessCode
import java.util.*

data class Party(
    val id: UUID,
    val name: String,
    val gameMasterId: String
) {
    private val users = setOf(gameMasterId);
    private val accessCode = generateAccessCode();

    init {
        require(id.version() == 4) {"Party identifier must be UUIDv4"}
        require(name.isNotEmpty()) {"Party name must not be empty"}
        require(gameMasterId.isNotEmpty()) {"Game master must not be empty"}
    }

    fun getInvitation() =
        InvitationToken(id, accessCode)
}
