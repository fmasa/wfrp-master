package cz.muni.fi.rpg.model.domain.inventory

import cz.muni.fi.rpg.model.domain.character.CharacterId

class InventoryItemNotFound(itemId: InventoryItemId, characterId: CharacterId, cause: Throwable?) :
    Exception("Inventory item $itemId was not found for character $characterId", cause)