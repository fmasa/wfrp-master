package cz.frantisekmasa.wfrp_master.common.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberImagePainter
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun CharacterAvatar(
    url: String?,
    size: ItemIcon.Size,
    modifier: Modifier = Modifier,
    fallback: Resources.Drawable = Resources.Drawable.DefaultAvatarIcon,
    zoomable: Boolean = false,
) {
    Box(modifier) {
        when (url) {
            null -> ItemIcon(fallback, size = size)
            else -> {
                var dialogVisible by remember { mutableStateOf(false) }

                if (dialogVisible) {
                    AlertDialog(
                        onDismissRequest = { dialogVisible = false },
                        text = {
                            Image(
                                rememberImagePainter(url).value,
                                null,
                                Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                            )
                        },
                        confirmButton = {
                            TextButton(onClick = { dialogVisible = false }) {
                                Text(stringResource(Str.common_ui_button_dismiss).uppercase())
                            }
                        }
                    )
                }

                ItemIcon(
                    url = url,
                    size = size,
                    modifier = if (zoomable)
                        Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(
                                bounded = false,
                            ),
                            onClick = { dialogVisible = true },
                        )
                    else Modifier
                )
            }
        }
    }
}
