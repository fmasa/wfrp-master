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
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.auth.LocalUser
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Invitation
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.LocalPersistentSnackbarHolder
import dev.icerock.moko.resources.compose.stringResource
import io.ktor.http.URLBuilder
import io.ktor.http.Url

class InvitationLinkScreen(
    private val url: String,
) : Screen {
    @Composable
    override fun Content() {
        val navigation = LocalNavigationTransaction.current

        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = { BackButton() },
                    title = { Text(stringResource(Str.parties_title_join)) },
                )
            },
        ) {
            val screenModel: InvitationScreenModel = rememberScreenModel()

            val userId = LocalUser.current.id
            val parties =
                remember { screenModel.userParties(userId) }
                    .collectWithLifecycle(null)
                    .value

            if (parties == null) {
                FullScreenProgress()
                return@Scaffold
            }

            var invitation: Invitation? by remember { mutableStateOf(null) }

            val snackbarHolder = LocalPersistentSnackbarHolder.current
            val invalidInvitationError = stringResource(Str.messages_invitation_error_invitation_invalid)

            LaunchedEffect(url) {
                val loadedInvitation =
                    Url(url).parameters[QUERY_PARAMETER]?.let {
                        screenModel.deserializeInvitation(it)
                    }

                if (loadedInvitation != null) {
                    invitation = loadedInvitation
                } else {
                    snackbarHolder.showSnackbar(
                        invalidInvitationError,
                        duration = SnackbarDuration.Long,
                    )
                    navigation.goBack()
                }
            }

            invitation?.let {
                InvitationConfirmation(
                    invitation = it,
                    screenModel = screenModel,
                    onError = navigation::goBack,
                    onSuccess = navigation::goBack,
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
