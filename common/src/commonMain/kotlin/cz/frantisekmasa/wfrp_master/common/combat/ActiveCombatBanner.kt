package cz.frantisekmasa.wfrp_master.common.combat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun ActiveCombatBanner(party: Party) {
    party.activeCombat ?: return

    Surface(elevation = 8.dp) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(Spacing.bodyPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(stringResource(Str.combat_messages_combat_in_progress))

            val navigation = LocalNavigationTransaction.current

            TextButton(onClick = { navigation.navigate(ActiveCombatScreen(party.id)) }) {
                Text(stringResource(Str.common_ui_button_open).uppercase())
            }
        }
    }
}
