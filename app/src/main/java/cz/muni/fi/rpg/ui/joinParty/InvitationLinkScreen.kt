package cz.muni.fi.rpg.ui.joinParty

import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cz.frantisekmasa.wfrp_master.common.core.auth.LocalUser
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Invitation
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.viewModel.PremiumViewModel
import cz.frantisekmasa.wfrp_master.common.core.viewModel.providePremiumViewModel
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import cz.frantisekmasa.wfrp_master.navigation.Route
import cz.frantisekmasa.wfrp_master.navigation.Routing
import cz.muni.fi.rpg.ui.common.BuyPremiumPrompt
import cz.muni.fi.rpg.viewModels.provideJoinPartyViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun InvitationLinkScreen(routing: Routing<Route.InvitationLink>) {
    val scaffoldState = rememberScaffoldState()
    val strings = LocalStrings.current
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                navigationIcon = { BackButton(onClick = { routing.pop() }) },
                title = { Text(strings.parties.titleJoin) }
            )
        },
    ) {
        val invitationJson = routing.route.invitationJson
        val viewModel = provideJoinPartyViewModel()
        val premiumViewModel = providePremiumViewModel()

        val userId = LocalUser.current.id
        val parties = remember { viewModel.userParties(userId) }
            .collectWithLifecycle(null)
            .value

        if (parties == null) {
            FullScreenProgress()
            return@Scaffold
        }

        if (parties.size >= PremiumViewModel.FREE_PARTY_COUNT && premiumViewModel.active != true) {
            BuyPremiumPrompt(onDismissRequest = { routing.pop() })
            return@Scaffold
        }

        var invitation: Invitation? by remember { mutableStateOf(null) }

        LaunchedEffect(invitationJson) {
            withContext(Dispatchers.Default) {
                val loadedInvitation = viewModel.deserializeInvitationJson(invitationJson)

                if (loadedInvitation != null) {
                    invitation = loadedInvitation
                } else {
                    scaffoldState.snackbarHostState.showSnackbar(
                        strings.messages.invitationErrorInvitationInvalid,
                        duration = SnackbarDuration.Long,
                    )
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
