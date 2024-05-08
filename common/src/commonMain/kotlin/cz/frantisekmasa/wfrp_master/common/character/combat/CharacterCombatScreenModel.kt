package cz.frantisekmasa.wfrp_master.common.character.combat

import cafe.adriel.voyager.core.model.ScreenModel
import cz.frantisekmasa.wfrp_master.common.combat.domain.EquippedWeapon
import cz.frantisekmasa.wfrp_master.common.combat.domain.WornArmourPiece
import cz.frantisekmasa.wfrp_master.common.core.domain.HitLocation
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Armour
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItemRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.WeaponEquip
import cz.frantisekmasa.wfrp_master.common.core.utils.right
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class CharacterCombatScreenModel(
    characterId: CharacterId,
    trappingRepository: InventoryItemRepository,
    characterRepository: CharacterRepository,
) : ScreenModel {
    private val trappingsFlow = trappingRepository.findAllForCharacter(characterId)
    private val characterFlow =
        characterRepository.getLive(characterId)
            .right()
    private val strengthBonusFlow =
        characterFlow
            .map { it.characteristics.strengthBonus }
            .distinctUntilChanged()

    val toughnessBonus: Flow<Int> =
        characterFlow
            .map { it.characteristics.toughnessBonus }
            .distinctUntilChanged()

    val equippedWeapons: Flow<List<Pair<WeaponEquip, List<EquippedWeapon>>>> =
        trappingsFlow
            .combine(strengthBonusFlow, ::Pair)
            .map { (trappings, strengthBonus) ->
                trappings
                    .asSequence()
                    .mapNotNull { EquippedWeapon.fromTrappingOrNull(it, strengthBonus) }
                    .sortedBy { it.trapping.name }
                    .groupBy { it.equip }
                    .asSequence()
                    .sortedBy { it.key }
                    .map { it.key to it.value }
                    .toList()
            }

    val armour: Flow<Armour> = trappingsFlow.map { items -> Armour.fromItems(items) }

    val armourPieces: Flow<Map<HitLocation, List<WornArmourPiece>>> =
        trappingsFlow.map { trappings ->
            val locations = mutableMapOf<HitLocation, MutableList<WornArmourPiece>>()

            trappings
                .asSequence()
                .mapNotNull(WornArmourPiece::fromTrappingOrNull)
                .sortedBy { it.trapping.name }
                .forEach { piece ->
                    piece.armour.locations.forEach { location ->
                        locations.getOrPut(location) { mutableListOf() } += (piece)
                    }
                }

            locations
        }
}
