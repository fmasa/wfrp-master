package cz.frantisekmasa.wfrp_master.common.invitation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Invitation
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

@Composable
fun InvitationDialog(
    invitation: Invitation,
    screenModel: InvitationScreenModel,
    onDismissRequest: () -> Unit,
) {
    val strings = LocalStrings.current

    FullScreenDialog(onDismissRequest = onDismissRequest) {
        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = { CloseButton(onClick = onDismissRequest) },
                    title = { Text(strings.parties.titleInvitePlayers) },
                )
            }
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(Spacing.bodyPadding),
                verticalArrangement = Arrangement.spacedBy(Spacing.small, Alignment.CenterVertically)
            ) {
                InvitationDialogContent(invitation, screenModel)
            }
        }
    }
}
