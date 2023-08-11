package cz.frantisekmasa.wfrp_master.common.invitation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Invitation
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Invitations are platform specific.
 * Every platform should at least provide invitation link which can be used from all platforms.
 */
@Composable
actual fun InvitationDialogContent(
    invitation: Invitation,
    screenModel: InvitationScreenModel
) {
    val (json, setJson) = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(invitation) {
        withContext(Dispatchers.IO) {
            setJson(screenModel.serializeInvitation(invitation))
        }
    }

    if (json == null) {
        FullScreenProgress()
        return
    }

    Text(
        stringResource(Str.parties_messages_invitation_url_description),
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.caption,
    )

    SelectionContainer {
        Text(InvitationLinkScreen.deepLink(json).toString())
    }
}
