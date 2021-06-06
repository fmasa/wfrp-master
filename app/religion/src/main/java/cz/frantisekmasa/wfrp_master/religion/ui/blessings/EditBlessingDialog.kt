package cz.frantisekmasa.wfrp_master.religion.ui.blessings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.core.ui.primitives.SingleLineTextValue
import cz.frantisekmasa.wfrp_master.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.religion.R
import cz.frantisekmasa.wfrp_master.religion.domain.Blessing
import cz.frantisekmasa.wfrp_master.religion.ui.blessings.NonCompendiumBlessingForm
import java.util.*


@Composable
internal fun EditBlessingDialog(
    viewModel: BlessingsViewModel,
    blessingId: UUID,
    onDismissRequest: () -> Unit
) {
    val blessing =
        viewModel.items.observeAsState().value?.firstOrNull { it.id == blessingId } ?: return

    FullScreenDialog(onDismissRequest = onDismissRequest) {
        if (blessing.compendiumId != null) {
            BlessingDetail(
                blessing = blessing,
                onDismissRequest = onDismissRequest,
            )
        } else {
            NonCompendiumBlessingForm(
                viewModel = viewModel,
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
                SingleLineTextValue(R.string.label_range, blessing.range)
                SingleLineTextValue(R.string.label_target, blessing.target)
                SingleLineTextValue(R.string.label_duration, blessing.duration)

                Text(blessing.effect, Modifier.padding(top = 8.dp))
            }
        }
    }
}
