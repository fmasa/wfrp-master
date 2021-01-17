package cz.muni.fi.rpg.ui.joinParty

import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.res.stringResource
import cz.frantisekmasa.wfrp_master.core.domain.party.Invitation
import cz.frantisekmasa.wfrp_master.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.core.ui.primitives.longToast
import cz.frantisekmasa.wfrp_master.navigation.Route
import cz.frantisekmasa.wfrp_master.navigation.Routing
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.viewModels.provideJoinPartyViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun InvitationLinkScreen(routing: Routing<Route.InvitationLink>) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { BackButton(onClick = { routing.pop() }) },
                title = { Text(stringResource(R.string.title_joinParty)) }
            )
        },
    ) {
        val context = AmbientContext.current
        val invitationJson = routing.route.invitationJson
        val viewModel = provideJoinPartyViewModel()

        var invitation: Invitation? by remember { mutableStateOf(null) }

        LaunchedEffect(invitationJson) {
            withContext(Dispatchers.Default) {
                val loadedInvitation = viewModel.deserializeInvitationJson(invitationJson)

                if (loadedInvitation != null) {
                    invitation = loadedInvitation
                } else {
                    longToast(context, R.string.error_invalid_invitation)
                    withContext(Dispatchers.Main) { routing.pop() }
                }
            }
        }

        invitation?.let {
            InvitationConfirmation(
                invitation = it,
                viewModel = viewModel,
                onError = { routing.pop() },
                onSuccess = { routing.pop() },
            )
        }
    }
}