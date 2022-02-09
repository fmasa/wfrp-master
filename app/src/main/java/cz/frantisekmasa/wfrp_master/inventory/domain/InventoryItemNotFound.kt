package cz.frantisekmasa.wfrp_master.inventory.domain

import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId

class InventoryItemNotFound(itemId: InventoryItemId, characterId: CharacterId, cause: Throwable?) :
    Exception("Inventory item $itemId was not found for character $characterId", cause)
