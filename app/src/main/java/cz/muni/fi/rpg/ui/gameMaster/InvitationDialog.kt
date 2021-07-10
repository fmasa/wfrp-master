package cz.muni.fi.rpg.ui.gameMaster

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.fasterxml.jackson.databind.json.JsonMapper
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.ktx.Firebase
import cz.frantisekmasa.wfrp_master.core.domain.party.Invitation
import cz.frantisekmasa.wfrp_master.core.ui.primitives.VisualOnlyIconDescription
import cz.frantisekmasa.wfrp_master.navigation.Route
import cz.muni.fi.rpg.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.koin.core.context.KoinContextHandler

@Composable
internal fun InvitationDialog2(invitation: Invitation, onDismissRequest: () -> Unit) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(shape = MaterialTheme.shapes.medium) {
            Column(Modifier.padding(20.dp)) {
                Text(
                    stringResource(R.string.invitation_code_description),
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    style = MaterialTheme.typography.h6,
                )

                val sharingOptions = sharingOptions(invitation).collectAsState().value

                if (sharingOptions == null) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    val context = LocalContext.current

                    QrCode(sharingOptions.json)

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            startInvitationSendingIntent(context, invitation, sharingOptions.link)
                        },
                    ) {
                        Icon(painterResource(R.drawable.ic_share), VisualOnlyIconDescription)
                        Text(stringResource(R.string.share_link).toUpperCase(Locale.current))
                    }
                }
            }
        }
    }
}

@Composable
private fun sharingOptions(invitation: Invitation): StateFlow<SharingOptions?> {
    val jsonMapper: JsonMapper = KoinContextHandler.get().get()
    val flow = remember { MutableStateFlow<SharingOptions?>(null) }

    LaunchedEffect(invitation) {
        withContext(Dispatchers.IO) {
            val json = jsonMapper.writeValueAsString(invitation)
            val link = Firebase.dynamicLinks.shortLinkAsync {
                link = Route.InvitationLink(json).toDeepLink()
                androidParameters { }
                domainUriPrefix = "https://wfrp.page.link"
            }

            flow.value = SharingOptions(
                link = link.await().shortLink.toString(),
                json = json,
            )
        }
    }

    return flow
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

    Firebase.analytics.logEvent(FirebaseAnalytics.Event.SHARE) {
        param(FirebaseAnalytics.Param.CONTENT_TYPE, "party_invitation")
        param(FirebaseAnalytics.Param.ITEM_ID, invitation.partyId.toString())
        param(FirebaseAnalytics.Param.METHOD, "link")
    }
}
