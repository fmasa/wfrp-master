package cz.frantisekmasa.wfrp_master.navigation

import com.github.zsoltk.compose.router.BackStack

data class Routing<T : Route>(
    val route: T,
    val backStack: BackStack<Route>,
)