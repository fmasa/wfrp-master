package cz.frantisekmasa.wfrp_master.common.character

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.core.auth.LocalUser
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.ui.CharacterAvatar
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.DialogTitle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.utils.launchLogged
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import kotlinx.coroutines.Dispatchers

@Composable
fun UnassignedCharacterPickerDialog(
    partyId: PartyId,
    unassignedCharacters: List<Character>,
    screenModel: CharacterPickerScreenModel,
    onDismissRequest: () -> Unit,
    onAssigned: (CharacterId) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        buttons = {},
        text = {
            val coroutineScope = rememberCoroutineScope()
            val (saving, setSaving) = remember { mutableStateOf(false) }

            if (saving) {
                LinearProgressIndicator(Modifier.fillMaxWidth())
                return@AlertDialog
            }

            Column {
                DialogTitle(LocalStrings.current.character.titleSelectCharacter)

                Spacer(Modifier.height(Spacing.large))

                LazyColumn {
                    items(unassignedCharacters, key = { it.id }) { character ->
                        val userId = LocalUser.current.id

                        ListItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    coroutineScope.launchLogged(Dispatchers.IO) {
                                        setSaving(true)
                                        screenModel.assignCharacter(character, userId)
                                        onAssigned(CharacterId(partyId, character.id))
                                    }
                                },
                            icon = { CharacterAvatar(character.avatarUrl, ItemIcon.Size.Small) },
                            text = { Text(character.name) },
                        )
                    }
                }
            }
        }
    )
}
