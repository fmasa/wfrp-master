package cz.frantisekmasa.wfrp_master.common.core.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.github.aakira.napier.Napier

val LocalNavigationTransaction =
    staticCompositionLocalOf<NavigationTransaction> {
        error("NavigationTransaction was not set")
    }

/**
 * Prevents double navigation when multiple Navigator pushes are attempted in quick succession.
 * This can be e.g. user clicking button multiple times.
 */
class NavigationTransaction(
    val currentScreen: Screen,
    private val navigator: Navigator,
) {
    val canPop: Boolean get() = navigator.canPop

    fun navigate(screen: Screen) {
        if (navigator.lastItemOrNull != currentScreen || navigator.lastItemOrNull == screen) {
            return
        }

        Napier.d("Navigating to $screen", tag = TAG)
        navigator.push(screen)
    }

    fun replace(screen: Screen) {
        if (navigator.lastItemOrNull != currentScreen) {
            return
        }

        Napier.d("Replaced current screen to $screen", tag = TAG)
        navigator.replace(screen)
    }

    fun goBack(popLast: Boolean = true) {
        if (navigator.lastItemOrNull != currentScreen) {
            return
        }

        if (!popLast && navigator.size < 2) {
            return
        }

        Napier.d("Going back to previous screen", tag = TAG)
        navigator.pop()
    }

    fun goBackTo(predicate: (Screen) -> Boolean) {
        if (navigator.lastItemOrNull != currentScreen) {
            return
        }

        Napier.d("Going back to specific previous screen", tag = TAG)
        navigator.popUntil(predicate)
    }

    companion object {
        private const val TAG = "navigation"
    }
}

@Composable
fun ProvideNavigationTransaction(
    screen: Screen,
    content: @Composable () -> Unit,
) {
    val navigator = LocalNavigator.currentOrThrow
    val transaction = NavigationTransaction(screen, navigator)

    CompositionLocalProvider(
        LocalNavigationTransaction provides transaction,
        content = content,
    )
}
