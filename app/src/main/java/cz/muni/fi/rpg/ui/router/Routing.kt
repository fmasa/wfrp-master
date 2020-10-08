package cz.muni.fi.rpg.ui.router

import com.github.zsoltk.compose.router.BackStack

data class Routing<T : Route>(
    val route: T,
    val backStack: BackStack<Route>,
)