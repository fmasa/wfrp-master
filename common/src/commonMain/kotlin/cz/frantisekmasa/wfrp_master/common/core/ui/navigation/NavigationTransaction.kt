package cz.frantisekmasa.wfrp_master.common.core.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow

val LocalNavigationTransaction = staticCompositionLocalOf<NavigationTransaction> {
    error("NavigationTransaction was not set")
}

/**
 * Prevents double navigation when multiple Navigator pushes are attempted in quick succession.
 * This can be e.g. user clicking button multiple times.
 */
class NavigationTransaction(
    private val currentScreen: Screen,
    private val navigator: Navigator,
) {
    val canPop: Boolean get() = navigator.canPop

    fun navigate(screen: Screen) {
        if (navigator.lastItemOrNull != currentScreen || navigator.lastItemOrNull == screen) {
            return
        }

        navigator.push(screen)
    }

    fun replace(screen: Screen) {
        if (navigator.lastItemOrNull != currentScreen) {
            return
        }

        navigator.replace(screen)
    }

    fun goBack() {
        if (navigator.lastItemOrNull != currentScreen) {
            return
        }

        navigator.pop()
    }

    fun goBackTo(predicate: (Screen) -> Boolean) {
        if (navigator.lastItemOrNull != currentScreen) {
            return
        }

        navigator.popUntil(predicate)
    }
}

@Composable
fun ProvideNavigationTransaction(screen: Screen, content: @Composable () -> Unit) {
    val navigator = LocalNavigator.currentOrThrow
    val transaction = NavigationTransaction(screen, navigator)

    CompositionLocalProvider(
        LocalNavigationTransaction provides transaction,
        content = content,
    )
}
