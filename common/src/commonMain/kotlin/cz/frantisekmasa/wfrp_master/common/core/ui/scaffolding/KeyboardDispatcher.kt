package cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.key

class KeyboardDispatcher {
    private val afterChildrenListeners = mutableMapOf<Any?, (KeyEvent) -> Boolean>()
    private val beforeChildrenListeners = mutableMapOf<Any?, (KeyEvent) -> Boolean>()

    private fun listeners(beforeChildren: Boolean): MutableMap<Any?, (KeyEvent) -> Boolean> {
        return if (beforeChildren) {
            beforeChildrenListeners
        } else {
            afterChildrenListeners
        }
    }

    fun register(
        key: Any?,
        beforeChildren: Boolean,
        effect: (KeyEvent) -> Boolean,
    ) {
        listeners(beforeChildren)[key] = effect
    }

    fun deregister(
        key: Any?,
        beforeChildren: Boolean,
        effect: (KeyEvent) -> Boolean,
    ) {
        listeners(beforeChildren).remove(key, effect)
    }

    fun dispatch(
        event: KeyEvent,
        beforeChildren: Boolean,
    ): Boolean {
        return listeners(beforeChildren).any { it.value(event) }
    }
}

val LocalKeyboardDispatcher = staticCompositionLocalOf { KeyboardDispatcher() }

@Composable
fun KeyboardEffect(
    key: Any?,
    beforeChildren: Boolean = false,
    effect: (KeyEvent) -> Boolean,
) {
    val dispatcher = LocalKeyboardDispatcher.current

    DisposableEffect(key, dispatcher, effect) {
        dispatcher.register(key, beforeChildren, effect)
        onDispose { dispatcher.deregister(key, beforeChildren, effect) }
    }
}
