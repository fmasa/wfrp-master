package cz.frantisekmasa.wfrp_master.common.gameMaster

import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character

sealed class Player {
    data class UserWithoutCharacter(val userId: String) : Player()
    data class ExistingCharacter(val character: Character) : Player()
}
