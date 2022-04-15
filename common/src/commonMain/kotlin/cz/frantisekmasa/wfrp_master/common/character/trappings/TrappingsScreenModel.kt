package cz.frantisekmasa.wfrp_master.common.character.trappings

import androidx.compose.runtime.Immutable
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import cz.frantisekmasa.wfrp_master.common.core.domain.Money
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterFeatureRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.character.NotEnoughMoney
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Armour
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Encumbrance
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItem
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItemRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.sum
import cz.frantisekmasa.wfrp_master.common.core.shared.IO
import cz.frantisekmasa.wfrp_master.common.core.utils.right
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


class TrappingsScreenModel(
    private val characterId: CharacterId,
    private val inventoryItems: InventoryItemRepository,
    private val armorRepository: CharacterFeatureRepository<Armour>,
    private val characters: CharacterRepository
) : ScreenModel {

    private val character = characters.getLive(characterId).right()
    val inventory: Flow<List<InventoryItem>> = inventoryItems.findAllForCharacter(characterId)

    val maxEncumbrance: Flow<Encumbrance> =
        character.map { Encumbrance.maximumForCharacter(it.characteristics) }

    val totalEncumbrance: Flow<Encumbrance?> = inventory
        .map { items -> items.map { it.effectiveEncumbrance }.sum() }

    val armor: Flow<EquippedArmour> =
        armorRepository
            .getLive(characterId)
            .right()
            .combine(inventory) { armour, items ->
                EquippedArmour(
                    armourFromItems = Armour.fromItems(items),
                    legacyArmour = armour,
                )
            }

    @Immutable
    data class EquippedArmour(
        val armourFromItems: Armour,
        val legacyArmour: Armour,
    )

    val money: Flow<Money> = character.map { it.money }

    suspend fun addMoney(amount: Money) {
        val character = characters.get(characterId)
        try {
            characters.save(characterId.partyId, character.addMoney(amount))
        } catch (e: IllegalArgumentException) {
        }
    }

    /**
     * @throws NotEnoughMoney
     */
    suspend fun subtractMoney(amount: Money) {
        val character = characters.get(characterId)
        characters.save(characterId.partyId, character.subtractMoney(amount))
    }

    suspend fun saveInventoryItem(inventoryItem: InventoryItem) {
        inventoryItems.save(characterId, inventoryItem)
    }

    fun removeInventoryItem(inventoryItem: InventoryItem) = coroutineScope.launch(Dispatchers.IO) {
        inventoryItems.remove(characterId, inventoryItem.id)
    }

    fun updateArmor(armor: Armour) = coroutineScope.launch(Dispatchers.IO) {
        armorRepository.save(characterId, armor)
    }
}
