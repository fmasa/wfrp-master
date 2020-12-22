package cz.muni.fi.rpg.model.domain.inventory

import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId

class InventoryItemNotFound(itemId: InventoryItemId, characterId: CharacterId, cause: Throwable?) :
    Exception("Inventory item $itemId was not found for character $characterId", cause)