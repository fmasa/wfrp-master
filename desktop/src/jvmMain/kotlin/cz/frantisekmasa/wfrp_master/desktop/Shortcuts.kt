package cz.frantisekmasa.wfrp_master.desktop

import androidx.compose.runtime.Composable
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.KeyboardEffect

@Composable
fun Shortcuts() {
    val transaction = LocalNavigationTransaction.current

    KeyboardEffect("global-shortcuts") {
        if (!it.isAltPressed || it.key != Key.DirectionLeft || it.type != KeyEventType.KeyDown) {
            return@KeyboardEffect false
        }

        transaction.goBack(popLast = false)

        return@KeyboardEffect true
    }
}
