package cz.frantisekmasa.wfrp_master.common.character.combat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.character.trappings.TrappingDetailScreen
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItem
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing

@Composable
fun CharacterCombatScreen(
    characterId: CharacterId,
    screenModel: CharacterCombatScreenModel,
    modifier: Modifier,
) {
    val armour = screenModel.armour.collectWithLifecycle(null).value
    val armourPieces = screenModel.armourPieces.collectWithLifecycle(null).value
    val weapons = screenModel.equippedWeapons.collectWithLifecycle(null).value
    val toughnessBonus = screenModel.toughnessBonus.collectWithLifecycle(null).value

    if (armour == null || armourPieces == null || weapons == null || toughnessBonus == null) {
        FullScreenProgress()
        return
    }

    Column(
        modifier
            .background(MaterialTheme.colors.background)
            .verticalScroll(rememberScrollState())
            .padding(top = Spacing.small),
    ) {
        val navigation = LocalNavigationTransaction.current
        val onTrappingClick: (InventoryItem) -> Unit = {
            navigation.navigate(TrappingDetailScreen(characterId, it.id))
        }

        WeaponsCard(weapons, onTrappingClick = onTrappingClick)
        ArmourCard(armour, armourPieces, toughnessBonus, onTrappingClick = onTrappingClick)
    }
}
