package cz.frantisekmasa.wfrp_master.common.character.religion.blessings.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.character.religion.blessings.BlessingsScreenModel
import cz.frantisekmasa.wfrp_master.common.core.domain.religion.Blessing
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.text.SingleLineTextValue
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

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

@Composable
private fun BlessingDetail(
    blessing: Blessing,
    onDismissRequest: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    CloseButton(onClick = onDismissRequest)
                },
                title = { Text(blessing.name) },
            )
        }
    ) {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            Column(Modifier.padding(Spacing.bodyPadding)) {
                val strings = LocalStrings.current.blessings

                SingleLineTextValue(strings.labelRange, blessing.range)
                SingleLineTextValue(strings.labelTarget, blessing.target)
                SingleLineTextValue(strings.labelDuration, blessing.duration)

                Text(blessing.effect, Modifier.padding(top = 8.dp))
            }
        }
    }
}
