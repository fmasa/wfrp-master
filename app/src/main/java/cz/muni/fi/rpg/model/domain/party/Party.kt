package cz.muni.fi.rpg.model.domain.party
import cz.muni.fi.rpg.model.domain.common.Ambitions
import cz.muni.fi.rpg.model.generateAccessCode
import java.util.*

data class Party(
    val id: UUID,
    val name: String,
    val gameMasterId: String,
    private var ambitions: Ambitions = Ambitions("", "")
) {
    companion object {
        const val NAME_MAX_LENGTH = 50
    }

    private val accessCode = generateAccessCode();
    val users = setOf(gameMasterId);

    init {
        require(id.version() == 4) {"Party identifier must be UUIDv4"}
        require(name.isNotBlank()) {"Party name must not be empty"}
        require(name.length <= NAME_MAX_LENGTH) {"Party name is too long"}
        require(gameMasterId.isNotEmpty()) {"Game master must not be empty"}
    }

    fun updateAmbitions(ambitions: Ambitions) {
        this.ambitions = ambitions
    }

    fun getAmbitions() = ambitions

    fun getInvitation() = Invitation(id, name, accessCode)
}
