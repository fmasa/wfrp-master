package cz.frantisekmasa.wfrp_master.common.character.religion.blessings.dialog

import androidx.compose.runtime.Composable
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.character.religion.blessings.BlessingDetail
import cz.frantisekmasa.wfrp_master.common.character.religion.blessings.BlessingsScreenModel
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle

@Composable
internal fun EditBlessingDialog(
    screenModel: BlessingsScreenModel,
    blessingId: Uuid,
    onDismissRequest: () -> Unit
) {
    val blessing = screenModel.items.collectWithLifecycle(null)
        .value
        ?.firstOrNull { it.id == blessingId }
        ?: return

    FullScreenDialog(onDismissRequest = onDismissRequest) {
        if (blessing.compendiumId != null) {
            BlessingDetail(
                blessing = blessing,
                onDismissRequest = onDismissRequest,
            )
        } else {
            NonCompendiumBlessingForm(
                screenModel = screenModel,
                existingBlessing = blessing,
                onDismissRequest = onDismissRequest
            )
        }
    }
}

