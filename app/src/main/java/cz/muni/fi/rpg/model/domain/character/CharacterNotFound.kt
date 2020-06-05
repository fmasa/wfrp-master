package cz.muni.fi.rpg.model.domain.character


class CharacterNotFound(characterId: CharacterId, cause: Throwable? = null) : Exception(
    "Character for user $characterId.userId was not found in party $characterId.partyId",
    cause
)