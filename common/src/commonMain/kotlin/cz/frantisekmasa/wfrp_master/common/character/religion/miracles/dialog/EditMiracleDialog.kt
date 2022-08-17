package cz.frantisekmasa.wfrp_master.common.character.religion.miracles.dialog

import androidx.compose.runtime.Composable
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.character.religion.miracles.MiracleDetail
import cz.frantisekmasa.wfrp_master.common.character.religion.miracles.MiraclesScreenModel
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle


@Composable
internal fun EditMiracleDialog(
    screenModel: MiraclesScreenModel,
    miracleId: Uuid,
    onDismissRequest: () -> Unit
) {
    val miracle =
        screenModel.items.collectWithLifecycle(null)
            .value
            ?.firstOrNull { it.id == miracleId }
            ?: return

    FullScreenDialog(onDismissRequest = onDismissRequest) {
        if (miracle.compendiumId != null) {
            MiracleDetail(
                miracle = miracle,
                onDismissRequest = onDismissRequest,
            )
        } else {
            NonCompendiumMiracleForm(
                screenModel = screenModel,
                existingMiracle = miracle,
                onDismissRequest = onDismissRequest
            )
        }
    }
}
