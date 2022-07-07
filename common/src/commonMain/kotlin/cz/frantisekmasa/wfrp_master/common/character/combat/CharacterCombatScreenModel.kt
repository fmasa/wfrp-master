package cz.frantisekmasa.wfrp_master.common.character.combat

import androidx.compose.runtime.Immutable
import cafe.adriel.voyager.core.model.ScreenModel
import cz.frantisekmasa.wfrp_master.common.core.domain.Damage
import cz.frantisekmasa.wfrp_master.common.core.domain.HitLocation
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Armour
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItem
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItemRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.TrappingType
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
    private val characterFlow = characterRepository.getLive(characterId)
        .right()
    private val strengthBonusFlow = characterFlow
        .map { it.characteristics.strengthBonus.toUInt() }
        .distinctUntilChanged()

    val toughnessBonus: Flow<UInt> = characterFlow
        .map { it.characteristics.toughnessBonus.toUInt() }
        .distinctUntilChanged()

    val equippedWeapons: Flow<List<Pair<WeaponEquip, List<EquippedWeapon>>>> =
        trappingsFlow
            .combine(strengthBonusFlow, ::Pair)
            .map { (trappings, strengthBonus) ->
                trappings
                    .asSequence()
                    .mapNotNull { equippedWeaponOrNull(it, strengthBonus) }
                    .sortedBy { it.trapping.name }
                    .groupBy { it.equip }
                    .asSequence()
                    .sortedBy { it.key }
                    .map { it.key to it.value }
                    .toList()
            }

    val armour: Flow<Armour> = trappingsFlow.map { items -> Armour.fromItems(items) }

    val armourPieces: Flow<Map<HitLocation, List<WornArmourPiece>>> = trappingsFlow.map { trappings ->
        val locations = mutableMapOf<HitLocation, MutableList<WornArmourPiece>>()

        trappings
            .asSequence()
            .mapNotNull(this::wornArmourOrNull)
            .sortedBy { it.trapping.name }
            .forEach { piece ->
                piece.armour.locations.forEach { location ->
                    locations.getOrPut(location) { mutableListOf() } += (piece)
                }
            }

        locations
    }

    @Immutable
    data class EquippedWeapon(
        val weapon: TrappingType.Weapon,
        val trapping: InventoryItem,
        val equip: WeaponEquip,
        val damage: Damage,
    )

    private fun wornArmourOrNull(trapping: InventoryItem): WornArmourPiece? {
        val type = trapping.trappingType

        if (type !is TrappingType.Armour || !type.worn) {
            return null
        }

        return WornArmourPiece(
            trapping = trapping,
            armour = type,
        )
    }

    @Immutable
    data class WornArmourPiece(
        val trapping: InventoryItem,
        val armour: TrappingType.Armour,
    )

    private fun equippedWeaponOrNull(
        trapping: InventoryItem,
        strengthBonus: UInt
    ): EquippedWeapon? {
        val type = trapping.trappingType

        if (type !is TrappingType.Weapon) {
            return null
        }

        val equip = type.equipped ?: return null

        return EquippedWeapon(
            trapping = trapping,
            weapon = type,
            equip = equip,
            damage = type.damage.calculate(
                strengthBonus = strengthBonus,
                successLevels = 0.toUInt(),
            )
        )
    }
}