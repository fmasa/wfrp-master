package cz.frantisekmasa.wfrp_master.combat.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import arrow.core.extensions.list.foldable.exists
import cz.frantisekmasa.wfrp_master.combat.R
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.core.viewModel.PartyViewModel
import cz.frantisekmasa.wfrp_master.core.viewModel.viewModel
import cz.frantisekmasa.wfrp_master.navigation.Route
import cz.frantisekmasa.wfrp_master.navigation.Routing
import org.koin.core.parameter.parametersOf
import java.util.*

@Composable
fun ActiveCombatBanner(partyId: PartyId, routing: Routing<*>) {
    val partyViewModel: PartyViewModel by viewModel { parametersOf(partyId) }

    if (routing.backStack.elements.contains(Route.ActiveCombat(partyId))) {
        // Prevent long and confusing back stack when user goes i.e.
        // combat -> character detail -> combat
        return
    }

    partyViewModel.party
        .collectAsState(null)
        .value
        ?.getActiveCombat() ?: return

    Surface(elevation = 8.dp) {
        Row(
            Modifier.fillMaxWidth().padding(Spacing.bodyPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(stringResource(R.string.combat_in_progress))
            TextButton(onClick = { routing.backStack.push(Route.ActiveCombat(partyId)) }) {
                Text(stringResource(R.string.button_open).toUpperCase(Locale.current))
            }
        }
    }
}