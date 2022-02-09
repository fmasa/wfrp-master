package cz.muni.fi.rpg.ui.partySettings

import androidx.compose.foundation.clickable
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import cz.muni.fi.rpg.ui.gameMaster.RenamePartyDialog

@Composable
fun PartyNameItem(partyName: String, viewModel: PartySettingsViewModel) {
    var dialogVisible by remember { mutableStateOf(false) }

    ListItem(
        text = { Text(LocalStrings.current.parties.labelName) },
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
