package cz.frantisekmasa.wfrp_master.common.partySettings

import androidx.compose.foundation.clickable
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.Str
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun PartyNameItem(
    partyName: String,
    viewModel: PartySettingsScreenModel,
) {
    var dialogVisible by remember { mutableStateOf(false) }

    ListItem(
        text = { Text(stringResource(Str.parties_label_name)) },
        secondaryText = { Text(partyName) },
        modifier = Modifier.clickable { dialogVisible = true },
    )

    if (dialogVisible) {
        RenamePartyDialog(
            currentName = partyName,
            viewModel = viewModel,
            onDismissRequest = { dialogVisible = false },
        )
    }
}
