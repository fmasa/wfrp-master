package cz.muni.fi.rpg.ui.common.composables

import androidx.compose.animation.DpPropKey
import androidx.compose.animation.core.*
import androidx.compose.animation.transition
import androidx.compose.foundation.Icon
import androidx.compose.foundation.layout.*
import androidx.compose.material.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawOpacity
import androidx.compose.ui.drawLayer
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import cz.muni.fi.rpg.R

enum class MenuState {
    COLLAPSED,
    EXPANDED,
}

private fun toState(state: MenuState) = when (state) {
    MenuState.COLLAPSED -> MenuState.EXPANDED
    MenuState.EXPANDED -> MenuState.COLLAPSED
}

private val opacity = FloatPropKey()
private val yOffset = DpPropKey()
private val iconRotation = FloatPropKey()

private const val animationLengthMillis = 200

private val transitionDefinition = transitionDefinition<MenuState> {
    //2
    state(MenuState.COLLAPSED) {
        this[opacity] = 0f
        this[yOffset] = 0.dp
        this[iconRotation] = 0f
    }
    //3
    state(MenuState.EXPANDED) {
        this[opacity] = 1f
        this[yOffset] = 20.dp
        this[iconRotation] = 90f + 45f
    }

    //4
    transition(MenuState.COLLAPSED to MenuState.EXPANDED) {
        opacity using tween(durationMillis = animationLengthMillis)
        yOffset using spring(Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessHigh)
        iconRotation using tween(durationMillis = animationLengthMillis)
    }

    //5
    transition(MenuState.EXPANDED to MenuState.COLLAPSED) {
        opacity using tween(durationMillis = animationLengthMillis)
        yOffset using tween(durationMillis = animationLengthMillis)
        iconRotation using tween(durationMillis = animationLengthMillis)
    }
}

@Composable
fun FloatingActionsMenu(
    state: MutableState<MenuState>,
    subButtons: @Composable ColumnScope.() -> Unit
) {
    val transition = transition(definition = transitionDefinition, toState = state.value)

    Column(horizontalAlignment = Alignment.End) {
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(transition[yOffset]),
            modifier = Modifier
                .drawOpacity(transition[opacity])
                .padding(bottom = transition[yOffset])
        ) {
            if (state.value == MenuState.EXPANDED) {
                subButtons()
            }
        }

        FloatingActionButton(
            onClick = { state.value = toState(state.value) },
        ) {
            Icon(
                vectorResource(R.drawable.ic_add),
                modifier = Modifier.drawLayer(rotationZ = transition[iconRotation])
            )
        }
    }
}