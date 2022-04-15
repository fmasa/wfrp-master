package cz.frantisekmasa.wfrp_master.common.invitation

import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cz.frantisekmasa.wfrp_master.common.core.auth.LocalUser
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Invitation
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.LocalPersistentSnackbarHolder
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import io.ktor.http.URLBuilder
import io.ktor.http.Url
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InvitationLinkScreen(
    private val url: Url,
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val strings = LocalStrings.current

        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = { BackButton(onClick = { navigator.pop() }) },
                    title = { Text(strings.parties.titleJoin) }
                )
            },
        ) {
            val screenModel: InvitationScreenModel = rememberScreenModel()

            val userId = LocalUser.current.id
            val parties = remember { screenModel.userParties(userId) }
                .collectWithLifecycle(null)
                .value

            if (parties == null) {
                FullScreenProgress()
                return@Scaffold
            }

            var invitation: Invitation? by remember { mutableStateOf(null) }

            val snackbarHolder = LocalPersistentSnackbarHolder.current

            LaunchedEffect(url) {
                val loadedInvitation = url.parameters[QUERY_PARAMETER]?.let {
                    screenModel.deserializeInvitation(it)
                }

                if (loadedInvitation != null) {
                    invitation = loadedInvitation
                } else {
                    snackbarHolder.showSnackbar(
                        strings.messages.invitationErrorInvitationInvalid,
                        duration = SnackbarDuration.Long,
                    )
                    navigator.pop()
                }
            }

            invitation?.let {
                InvitationConfirmation(
                    invitation = it,
                    screenModel = screenModel,
                    onError = { navigator.pop() },
                    onSuccess = { navigator.pop() },
                )
            }
        }
    }

    companion object {
        fun deepLink(invitationJson: String): Url {
            return URLBuilder("https://dnd-master-58fca.web.app/app/invitation").apply {
                parameters.append(QUERY_PARAMETER, invitationJson)
            }.build()
        }

        private const val QUERY_PARAMETER = "invitation"
    }
}
