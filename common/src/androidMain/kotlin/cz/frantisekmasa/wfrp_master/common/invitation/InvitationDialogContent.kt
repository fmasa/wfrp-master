package cz.frantisekmasa.wfrp_master.common.invitation

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.core.net.toUri
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.ktx.Firebase
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Invitation
import cz.frantisekmasa.wfrp_master.common.core.logging.Reporter
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.VisualOnlyIconDescription
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@Composable
actual fun InvitationDialogContent(invitation: Invitation, screenModel: InvitationScreenModel) {
    val (sharingOptions, setSharingOptions) = mutableStateOf<SharingOptions?>(null)

    LaunchedEffect(invitation) {
        setSharingOptions(buildSharingOptions(invitation, screenModel))
    }

    if (sharingOptions == null) {
        FullScreenProgress()
    } else {
        val context = LocalContext.current
        val strings = LocalStrings.current.parties

        Text(
            strings.messages.qrCodeDescription,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.caption,
        )

        QrCode(sharingOptions.json)

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                startInvitationSendingIntent(context, invitation, sharingOptions.link)
            },
        ) {
            Icon(Icons.Rounded.Share, VisualOnlyIconDescription)
            Text(strings.buttonShareLink.uppercase())
        }
    }
}

private suspend fun buildSharingOptions(
    invitation: Invitation,
    screenModel: InvitationScreenModel,
): SharingOptions {
    return withContext(Dispatchers.IO) {
        val json = screenModel.serializeInvitation(invitation)
        val link = Firebase.dynamicLinks.shortLinkAsync {
            link = InvitationLinkScreen.deepLink(json).toString().toUri()
            androidParameters { }
            domainUriPrefix = "https://wfrp.page.link"
        }

        SharingOptions(
            link = link.await().shortLink.toString(),
            json = json,
        )
    }
}

private data class SharingOptions(
    val link: String,
    val json: String,
)

private fun startInvitationSendingIntent(
    context: Context,
    invitation: Invitation,
    link: String
) {
    context.startActivity(
        Intent.createChooser(
            Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Join ${invitation.partyName} using this link: $link")
                type = "text/plain"
            },
            "Send link to your friends"
        )
    )

    Reporter.recordEvent(
        "share",
        mapOf(
            "content_type" to "party_invitation",
            "item_id" to invitation.partyId.toString(),
            "method" to "link",
        )
    )
}
