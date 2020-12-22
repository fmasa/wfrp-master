package cz.muni.fi.rpg.model.domain.character

import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId

class CharacterNotFound(characterId: CharacterId, cause: Throwable? = null) : Exception(
    "Character for user $characterId.userId was not found in party $characterId.partyId",
    cause
)