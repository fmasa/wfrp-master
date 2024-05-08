package cz.frantisekmasa.wfrp_master.common.core.domain.character

import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId

class CharacterNotFound(characterId: CharacterId, cause: Throwable? = null) : Exception(
    "Character for user $characterId.userId was not found in party $characterId.partyId",
    cause,
)
