package cz.frantisekmasa.wfrp_master.navigation

import com.github.zsoltk.compose.router.BackStack

data class Routing<T : Route>(
    val route: T,
    val backStack: BackStack<Route>,
) {
    /**
     * Pops all routes above given route
     *
     * @param inclusive pop given route as well
     */
    tailrec fun popUpTo(route: T, inclusive: Boolean = false) {
        if (backStack.size == 1) {
            return
        }

        if (backStack.last() == route) {
            if (inclusive) {
                backStack.pop()
            }

            return
        }

        popUpTo(route, inclusive)
    }
}