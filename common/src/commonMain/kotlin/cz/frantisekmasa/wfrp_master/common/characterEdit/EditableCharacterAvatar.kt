package cz.frantisekmasa.wfrp_master.common.characterEdit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.character.CharacterScreenModel
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.shared.FileType
import cz.frantisekmasa.wfrp_master.common.core.shared.rememberFileChooser
import cz.frantisekmasa.wfrp_master.common.core.ui.CharacterAvatar
import cz.frantisekmasa.wfrp_master.common.core.ui.menu.DropdownMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.menu.DropdownMenuItem
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.LocalPersistentSnackbarHolder
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch


@Composable
fun EditableCharacterAvatar(
    character: Character,
    screenModel: CharacterScreenModel,
    modifier: Modifier = Modifier
) {
    var active by remember { mutableStateOf(false) }
    var processing by remember { mutableStateOf(false) }

    Box(modifier, Alignment.Center) {
        CharacterAvatar(
            character.avatarUrl,
            ItemIcon.Size.XLarge,
            Modifier
                .background(MaterialTheme.colors.surface, CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = false),
                    onClick = { active = true },
                )
        )

        if (processing) {
            CircularProgressIndicator()
        }

        val coroutineScope = rememberCoroutineScope()
        val messages = LocalStrings.current.messages
        val snackbarHolder = LocalPersistentSnackbarHolder.current

        val fileChooser = rememberFileChooser { result ->
            result
                .onFailure { snackbarHolder.showSnackbar(messages.couldNotOpenFile) }
                .mapCatching { image ->
                    processing = true
                    screenModel.changeAvatar(image.readBytes())
                }.onFailure {
                    Napier.e(it.toString(), it)
                }
            processing = false
        }

        DropdownMenu(expanded = active, onDismissRequest = { active = false }) {
            DropdownMenuItem(
                onClick = {
                    active = false
                    fileChooser.open(FileType.IMAGE)
                },
            ) {
                Text("Change avatar")
            }
            DropdownMenuItem(
                onClick = {
                    active = false
                    coroutineScope.launch {
                        screenModel.removeAvatar()
                        snackbarHolder.showSnackbar(messages.avatarRemoved)
                    }
                },
            ) {
                Text("Remove avatar")
            }
        }
    }
}
