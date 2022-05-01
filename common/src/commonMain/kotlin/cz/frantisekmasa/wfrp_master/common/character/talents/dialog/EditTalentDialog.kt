package cz.frantisekmasa.wfrp_master.common.character.talents.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.character.talents.TalentsScreenModel
import cz.frantisekmasa.wfrp_master.common.core.shared.IO
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun EditTalentDialog(
    screenModel: TalentsScreenModel,
    talentId: Uuid,
    onDismissRequest: () -> Unit
) {
    val talent =
        screenModel.talents.collectWithLifecycle(null)
            .value
            ?.firstOrNull { it.id == talentId } ?: return

    FullScreenDialog(onDismissRequest = onDismissRequest) {
        if (talent.compendiumId != null) {
            val coroutineScope = rememberCoroutineScope()

            TalentDetail(
                talent = talent,
                onDismissRequest = onDismissRequest,
                onTimesTakenChange = { timesTaken ->
                    coroutineScope.launch(Dispatchers.IO) {
                        screenModel.saveTalent(talent.copy(taken = timesTaken))
                    }
                }
            )
        } else {
            NonCompendiumTalentForm(
                screenModel = screenModel,
                existingTalent = talent,
                onDismissRequest = onDismissRequest,
            )
        }
    }
}
