package cz.frantisekmasa.wfrp_master.common.character.combat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.character.trappings.CharacterTrappingDetailScreen
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItem
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing

@Composable
fun CharacterCombatScreen(
    characterId: CharacterId,
    state: CharacterCombatScreenState,
    modifier: Modifier,
) {
    Column(
        modifier
            .background(MaterialTheme.colors.background)
            .verticalScroll(rememberScrollState())
            .padding(top = Spacing.small),
    ) {
        val navigation = LocalNavigationTransaction.current
        val onTrappingClick: (InventoryItem) -> Unit = {
            navigation.navigate(CharacterTrappingDetailScreen(characterId, it.id))
        }

        WeaponsCard(state.equippedWeapons, onTrappingClick = onTrappingClick)
        ArmourCard(
            armourPoints = state.armourPoints,
            armourPieces = state.armourPieces,
            toughnessBonus = state.toughnessBonus,
            onTrappingClick = onTrappingClick,
        )
    }
}
