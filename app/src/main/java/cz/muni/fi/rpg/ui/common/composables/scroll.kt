package cz.muni.fi.rpg.ui.common.composables

import androidx.compose.animation.asDisposableClock
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.ScrollableRow
import androidx.compose.foundation.animation.defaultFlingConfig
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.savedinstancestate.rememberSavedInstanceState
import androidx.compose.ui.platform.AnimationClockAmbient

/**
 * TODO: Scroll to correct state on new screen width
 *
 * Create and [remember] the [ScrollState] based on the currently appropriate scroll
 * configuration to allow changing scroll position or observing scroll behavior.
 *
 * Learn how to control [ScrollableColumn] or [ScrollableRow]:
 * @sample androidx.compose.foundation.samples.ControlledScrollableRowSample
 *
 * @param initial initial scroller position to start with
 */
@Composable
fun rememberScrollState(initial: Float = 0f, vararg inputs: Any?): ScrollState {
    val clock = AnimationClockAmbient.current.asDisposableClock()
    val config = defaultFlingConfig()
    return rememberSavedInstanceState(
        clock, config, *inputs,
        saver = ScrollState.Saver(config, clock)
    ) {
        ScrollState(
            flingConfig = config,
            initial = initial,
            animationClock = clock
        )
    }
}
