package cz.frantisekmasa.wfrp_master.core.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Outline
import android.view.*
import androidx.compose.foundation.layout.Box
import androidx.compose.material.MaterialTheme
import cz.frantisekmasa.wfrp_master.core.R
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.*
import androidx.compose.ui.semantics.dialog
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.ViewTreeViewModelStoreOwner
import androidx.savedstate.ViewTreeSavedStateRegistryOwner
import cz.frantisekmasa.wfrp_master.core.ui.theme.LocalSystemUiController
import cz.frantisekmasa.wfrp_master.core.ui.theme.rememberSystemUiController


@Composable
fun FullScreenDialog(
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val composition = rememberCompositionContext()
    val currentContent by rememberUpdatedState(content)
    val lightTheme = MaterialTheme.colors.isLight
    val dialog = remember(view, density, lightTheme) {
        DialogWrapper(
            onDismissRequest,
            view,
            layoutDirection,
            density,
            lightTheme,
        ).apply {
            setContent(composition) {
                Box(Modifier.semantics { dialog() }) {
                    currentContent()
                }
            }
        }
    }

    DisposableEffect(dialog) {
        dialog.show()

        onDispose {
            dialog.dismiss()
            dialog.disposeComposition()
        }
    }

    SideEffect {
        dialog.updateParameters(
            onDismissRequest = onDismissRequest,
            layoutDirection = layoutDirection
        )
    }
}

/**
 * Provides the underlying window of a dialog.
 *
 * Implemented by dialog's root layout.
 */
interface DialogWindowProvider {
    val window: Window
}

@Suppress("ViewConstructor")
private class DialogLayout(
    context: Context,
    override val window: Window
) : AbstractComposeView(context), DialogWindowProvider {

    private var content: @Composable () -> Unit by mutableStateOf({})

    override var shouldCreateCompositionOnAttachedToWindow: Boolean = false
        private set

    fun setContent(parent: CompositionContext, content: @Composable () -> Unit) {
        setParentCompositionContext(parent)
        this.content = content
        shouldCreateCompositionOnAttachedToWindow = true
        createComposition()
    }

    @Composable
    override fun Content() {
        CompositionLocalProvider(
            LocalSystemUiController provides rememberSystemUiController(window),
            content = content,
        )
    }
}

private class DialogWrapper(
    private var onDismissRequest: () -> Unit,
    composeView: View,
    layoutDirection: LayoutDirection,
    density: Density,
    lightTheme: Boolean,
) : Dialog(
        composeView.context,
        if (lightTheme)
            R.style.AppTheme_Light_FullScreenDialog
        else R.style.AppTheme_Dark_FullScreenDialog
) {
    private val dialogLayout: DialogLayout

    private val maxSupportedElevation = 30.dp

    init {
        val window = window ?: error("Dialog has no window")
        window.requestFeature(Window.FEATURE_NO_TITLE)
        window.setWindowAnimations(R.style.AppTheme_Slide)
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialogLayout = DialogLayout(context, window).apply {
            // Enable children to draw their shadow by not clipping them
            clipChildren = false
        }

        fun ViewGroup.disableClipping() {
            clipChildren = false
            if (this is DialogLayout) return
            for (i in 0 until childCount) {
                (getChildAt(i) as? ViewGroup)?.disableClipping()
            }
        }

        // Turn of all clipping so shadows can be drawn outside the window
        (window.decorView as? ViewGroup)?.disableClipping()
        setContentView(dialogLayout)
        ViewTreeLifecycleOwner.set(dialogLayout, ViewTreeLifecycleOwner.get(composeView))
        ViewTreeViewModelStoreOwner.set(dialogLayout, ViewTreeViewModelStoreOwner.get(composeView))
        ViewTreeSavedStateRegistryOwner.set(
            dialogLayout,
            ViewTreeSavedStateRegistryOwner.get(composeView)
        )

        // Initial setup
        updateParameters(onDismissRequest, layoutDirection)
    }

    private fun setLayoutDirection(layoutDirection: LayoutDirection) {
        dialogLayout.layoutDirection = when (layoutDirection) {
            LayoutDirection.Ltr -> android.util.LayoutDirection.LTR
            LayoutDirection.Rtl -> android.util.LayoutDirection.RTL
        }
    }

    fun setContent(parentComposition: CompositionContext, children: @Composable () -> Unit) {
        dialogLayout.setContent(parentComposition, children)
    }

    fun updateParameters(onDismissRequest: () -> Unit, layoutDirection: LayoutDirection) {
        this.onDismissRequest = onDismissRequest
        setLayoutDirection(layoutDirection)
    }

    fun disposeComposition() {
        dialogLayout.disposeComposition()
    }

    override fun cancel() {
        // Prevents the dialog from dismissing itself
        return
    }

    override fun onBackPressed() {
        onDismissRequest()
    }
}
