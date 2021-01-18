package cz.muni.fi.rpg.ui.shell

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.onActive
import androidx.compose.runtime.onDispose
import androidx.compose.runtime.remember
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.KEY_ROUTE
import androidx.navigation.compose.rememberNavController
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import timber.log.Timber

@Composable
fun rememberNavControllerWithAnalytics(): NavHostController {
    val navController = rememberNavController()
    val listener = remember { DestinationAnalyticsLogger() }

    onActive {
        navController.addOnDestinationChangedListener(listener)
    }

    onDispose {
        navController.removeOnDestinationChangedListener(listener)
    }

    return navController
}

private class DestinationAnalyticsLogger : NavController.OnDestinationChangedListener {
    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        val route = arguments?.getString(KEY_ROUTE) ?: return

        Timber.d("Showing screen $route")

        Firebase.analytics.logEvent(
            FirebaseAnalytics.Event.SCREEN_VIEW,
            bundleOf(FirebaseAnalytics.Param.SCREEN_NAME to route)
        )
    }
}