package cz.frantisekmasa.wfrp_master.desktop

import androidx.compose.runtime.Composable
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import cafe.adriel.voyager.core.screen.Screen
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.KeyboardEffect

@Composable
fun Shortcuts(onNewWindowRequest: (initialScreen: Screen) -> Unit) {
    val transaction = LocalNavigationTransaction.current

    KeyboardEffect("global-shortcuts") {
        if (it.type != KeyEventType.KeyDown) {
            return@KeyboardEffect false
        }

        if (it.isAltPressed && it.key == Key.DirectionLeft) {
            transaction.goBack(popLast = false)
            return@KeyboardEffect true
        }

        if (it.isCtrlPressed && it.isShiftPressed && it.key == Key.T && transaction.canPop) {
            onNewWindowRequest(transaction.currentScreen)
            transaction.goBack()

            return@KeyboardEffect true
        }

        return@KeyboardEffect false
    }
}
