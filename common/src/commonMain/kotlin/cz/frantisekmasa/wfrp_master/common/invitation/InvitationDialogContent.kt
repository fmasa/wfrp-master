package cz.frantisekmasa.wfrp_master.common.invitation

import androidx.compose.runtime.Composable
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Invitation

/**
 * Invitations are platform specific.
 * Every platform should at least provide invitation link which can be used from all platforms.
 */
@Composable
expect fun InvitationDialogContent(invitation: Invitation, screenModel: InvitationScreenModel)