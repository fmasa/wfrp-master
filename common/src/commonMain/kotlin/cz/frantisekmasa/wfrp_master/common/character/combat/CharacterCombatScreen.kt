package cz.frantisekmasa.wfrp_master.common.character.combat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.character.trappings.InventoryItemDialog
import cz.frantisekmasa.wfrp_master.common.character.trappings.TrappingsScreenModel
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItem
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing

@Composable
fun CharacterCombatScreen(
    screenModel: CharacterCombatScreenModel,
    trappingsScreenModel: TrappingsScreenModel,
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

    var openedTrapping: InventoryItem? by remember { mutableStateOf(null) }

    if (openedTrapping != null) {
        InventoryItemDialog(
            trappingsScreenModel,
            openedTrapping,
            onDismissRequest = { openedTrapping = null },
        )
    }

    Column(
        modifier
            .background(MaterialTheme.colors.background)
            .verticalScroll(rememberScrollState())
            .padding(top = Spacing.small),
    ) {
        WeaponsCard(weapons, onTrappingClick = { openedTrapping = it })
        ArmourCard(armour, armourPieces, toughnessBonus, onTrappingClick = { openedTrapping = it })
    }
}
