package cz.muni.fi.rpg.ui.character.edit

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
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
import androidx.compose.ui.platform.LocalContext
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.ui.CharacterAvatar
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.LocalPersistentSnackbarHolder
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import cz.muni.fi.rpg.model.domain.CharacterAvatarChanger
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext

@Composable
fun EditableCharacterAvatar(
    characterId: CharacterId,
    character: Character,
    modifier: Modifier = Modifier
) {
    var active by remember { mutableStateOf(false) }
    var processing by remember { mutableStateOf(false) }

    Box(modifier, Alignment.Center) {
        CharacterAvatar(
            character.avatarUrl,
            ItemIcon.Size.XLarge,
            Modifier
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
        val context = LocalContext.current
        val messages = LocalStrings.current.messages
        val snackbarHolder = LocalPersistentSnackbarHolder.current

        val fileChooser = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri == null) {
                return@rememberLauncherForActivityResult
            }

            coroutineScope.launch(Dispatchers.IO) {
                try {
                    processing = true
                    val inputStream = context.contentResolver.openInputStream(uri)

                    if (inputStream == null) {
                        snackbarHolder.showSnackbar(messages.couldNotOpenFile)
                        return@launch
                    }

                    changer().changeAvatar(characterId, inputStream)
                    processing = false
                    snackbarHolder.showSnackbar(messages.avatarChanged)
                } catch (e: Throwable) {
                    Napier.e(e.toString(), e)
                    processing = false
                }
            }
        }

        DropdownMenu(expanded = active, onDismissRequest = { active = false }) {
            DropdownMenuItem(
                onClick = {
                    active = false
                    fileChooser.launch("image/*")
                },
            ) {
                Text("Change avatar")
            }
            DropdownMenuItem(
                onClick = {
                    active = false
                    coroutineScope.launch {
                        changer().removeAvatar(characterId)
                        snackbarHolder.showSnackbar(messages.avatarRemoved)
                    }
                },
            ) {
                Text("Remove avatar")
            }
        }
    }
}

private fun changer(): CharacterAvatarChanger = GlobalContext.get().get()