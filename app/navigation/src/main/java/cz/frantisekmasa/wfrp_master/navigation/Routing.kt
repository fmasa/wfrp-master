package cz.frantisekmasa.wfrp_master.navigation

import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.*

data class Routing<T : Route>(
    val route: T,
    private val navController: NavHostController,
) {
    /**
     * Pops all routes above given route
     *
     * @param inclusive pop given route as well
     */
    fun popUpTo(route: T, inclusive: Boolean = false) {
        navController.navigate(route.toString()) {
            popUpTo(route.toString()) { this.inclusive = inclusive }
        }
    }

    fun navigateTo(route: Route) {
        navController.navigate(route.toString())
    }

    fun pop() {
        navController.popBackStack()
    }

    fun replace(newRoute: Route) {
        navController.navigate(newRoute.toString()) {
            popUpTo(route.toString()) { inclusive = true }
        }
    }
}

fun NavHostController.navigate(route: Route, builder: NavOptionsBuilder.() -> Unit = {}) {
    navigate(route.toString(), builder)
}