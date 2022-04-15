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
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

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
            Text(LocalStrings.current.combat.messages.combatInProgress)

            val navigator = LocalNavigator.currentOrThrow

            TextButton(onClick = { navigator.push(ActiveCombatScreen(party.id)) }) {
                Text(LocalStrings.current.commonUi.buttonOpen.toUpperCase(Locale.current))
            }
        }
    }
}
