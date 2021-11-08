package cz.frantisekmasa.wfrp_master.religion.ui.miracles

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.core.ui.primitives.SingleLineTextValue
import cz.frantisekmasa.wfrp_master.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.SubheadBar
import cz.frantisekmasa.wfrp_master.religion.domain.Miracle
import cz.muni.fi.rpg.R
import java.util.UUID

@Composable
internal fun EditMiracleDialog(
    viewModel: MiraclesViewModel,
    miracleId: UUID,
    onDismissRequest: () -> Unit
) {
    val miracle =
        viewModel.items.collectWithLifecycle(null)
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
                viewModel = viewModel,
                existingMiracle = miracle,
                onDismissRequest = onDismissRequest
            )
        }
    }
}

@Composable
private fun MiracleDetail(
    miracle: Miracle,
    onDismissRequest: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    CloseButton(onClick = onDismissRequest)
                },
                title = { Text(miracle.name) },
            )
        }
    ) {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            SubheadBar {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(miracle.cultName)
                }
            }

            Column(Modifier.padding(Spacing.bodyPadding)) {
                SingleLineTextValue(R.string.label_range, miracle.range)
                SingleLineTextValue(R.string.label_target, miracle.target)
                SingleLineTextValue(R.string.label_duration, miracle.duration)

                Text(miracle.effect, Modifier.padding(top = 8.dp))
            }
        }
    }
}
