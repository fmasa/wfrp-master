package cz.frantisekmasa.wfrp_master.common.core.ui.dialogs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import cz.frantisekmasa.wfrp_master.common.core.ui.responsive.LocalScreenWidth
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.KeyboardDispatcher
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.KeyboardEffect
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.LocalKeyboardDispatcher

@Composable
actual fun FullScreenDialog(
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalKeyboardDispatcher provides remember { KeyboardDispatcher() },
    ) {
        val keyboardDispatcher = LocalKeyboardDispatcher.current
        Popup(
            popupPositionProvider =
                object : PopupPositionProvider {
                    override fun calculatePosition(
                        anchorBounds: IntRect,
                        windowSize: IntSize,
                        layoutDirection: LayoutDirection,
                        popupContentSize: IntSize,
                    ): IntOffset = IntOffset.Zero
                },
            focusable = true,
            onDismissRequest = onDismissRequest,
            onPreviewKeyEvent = {
                keyboardDispatcher.dispatch(it, beforeChildren = true)
            },
            onKeyEvent = {
                keyboardDispatcher.dispatch(it, beforeChildren = false)
            },
        ) {
            KeyboardEffect("dismiss") {
                if (it.key == Key.Escape && it.type == KeyEventType.KeyDown) {
                    onDismissRequest()
                    return@KeyboardEffect true
                }

                return@KeyboardEffect false
            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                LocalScreenWidth.current
                Surface(
                    elevation = 24.dp,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.padding(LocalScreenWidth.current * 0.05f),
                    content = content,
                )
            }
        }
    }
}
