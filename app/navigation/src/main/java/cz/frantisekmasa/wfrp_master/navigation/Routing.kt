package cz.frantisekmasa.wfrp_master.navigation

import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.*
import timber.log.Timber

data class Routing<T : Route>(
    val route: T,
    private val navController: NavHostController,
) {
    /**
     * Pops all routes above given route
     *
     * @param inclusive pop given route as well
     */
    fun popUpTo(route: Route, inclusive: Boolean = false) {
        navController.navigate(route.toString()) {
            popUpTo(route.toString()) { this.inclusive = inclusive }
        }
    }

    fun navigateTo(route: Route, popUpTo: Route? = null, inclusive: Boolean = false) {
        Timber.d("Navigating to $route")
        navController.navigate(route.toString()) {
            popUpTo?.let {
                popUpTo(it.toString()) { this.inclusive = inclusive }
            }
        }
    }

    fun pop() {
        navController.popBackStack()
    }
}

fun NavHostController.navigate(route: Route, builder: NavOptionsBuilder.() -> Unit = {}) {
    navigate(route.toString(), builder)
}