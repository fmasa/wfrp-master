package cz.frantisekmasa.wfrp_master.common.core.ui.primitives

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

enum class MenuState {
    COLLAPSED,
    EXPANDED,
}

private fun toState(state: MenuState) = when (state) {
    MenuState.COLLAPSED -> MenuState.EXPANDED
    MenuState.EXPANDED -> MenuState.COLLAPSED
}

private const val animationLengthMillis = 200

@Composable
fun FloatingActionsMenu(
    state: MenuState,
    onToggleRequest: (MenuState) -> Unit,
    icon: Painter,
    subButtons: @Composable ColumnScope.() -> Unit
) {
    val transition = updateTransition(state)

    val menuOpacity by transition.animateFloat({ tween(animationLengthMillis) }) { currentState ->
        when (currentState) {
            MenuState.COLLAPSED -> 0f
            MenuState.EXPANDED -> 1f
        }
    }

    val iconRotation by transition.animateFloat({ tween(animationLengthMillis) }) { currentState ->
        when (currentState) {
            MenuState.COLLAPSED -> 0f
            MenuState.EXPANDED -> 90f + 45f
        }
    }

    val menuYOffset by transition.animateDp({ tween(animationLengthMillis) }) { currentState ->
        when (currentState) {
            MenuState.COLLAPSED -> 0.dp
            MenuState.EXPANDED -> 20.dp
        }
    }

    Column(horizontalAlignment = Alignment.End, modifier = Modifier.fillMaxWidth()) {
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(menuYOffset),
            modifier = Modifier
                .alpha(menuOpacity)
                .padding(bottom = menuYOffset)
        ) {
            if (state == MenuState.EXPANDED) {
                subButtons()
            }
        }

        FloatingActionButton(
            onClick = { onToggleRequest(toState(state)) },
        ) {
            Icon(
                icon,
                LocalStrings.current.commonUi.iconToggleFabMenu,
                modifier = Modifier.graphicsLayer(rotationZ = iconRotation),
            )
        }
    }
}