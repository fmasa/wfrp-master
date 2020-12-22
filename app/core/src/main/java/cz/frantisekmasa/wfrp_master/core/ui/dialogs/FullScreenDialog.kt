package cz.frantisekmasa.wfrp_master.core.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.AmbientView
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.semantics.dialog
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.SecureFlagPolicy
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.ViewTreeViewModelStoreOwner
import androidx.savedstate.ViewTreeSavedStateRegistryOwner
import cz.frantisekmasa.wfrp_master.core.R


/**
 * Android specific properties to configure a dialog.
 *
 * @param securePolicy Policy for setting [WindowManager.LayoutParams.FLAG_SECURE] on the dialog's
 * window.
 */
@Immutable
data class AndroidDialogProperties(
    val securePolicy: SecureFlagPolicy = SecureFlagPolicy.Inherit
) : DialogProperties

/**
 * Opens a dialog with the given content.
 *
 * The dialog is visible as long as it is part of the composition hierarchy.
 * In order to let the user dismiss the Dialog, the implementation of [onDismissRequest] should
 * contain a way to remove to remove the dialog from the composition hierarchy.
 *
 * Example usage:
 *
 * @sample androidx.compose.ui.samples.DialogSample
 *
 * @param onDismissRequest Executes when the user tries to dismiss the Dialog.
 * @param properties Typically platform specific properties to further configure the dialog.
 * @param content The content to be displayed inside the dialog.
 */
@Composable
fun FullScreenDialog(
    onDismissRequest: () -> Unit,
    properties: DialogProperties? = null,
    content: @Composable () -> Unit
) {
    val view = AmbientView.current

    val lightTheme = MaterialTheme.colors.isLight

    val dialog = remember(view, lightTheme) {
        DialogWrapper(
            view,
            lightTheme
        )
    }
    dialog.onCloseRequest = onDismissRequest
    remember(properties) { dialog.setProperties(properties) }

    onActive {
        dialog.show()

        onDispose {
            dialog.dismiss()
            dialog.disposeComposition()
        }
    }

    val composition = compositionReference()
    onCommit {
        dialog.setContent(composition) {
            // TODO(b/159900354): draw a scrim and add margins around the Compose Dialog, and
            //  consume clicks so they can't pass through to the underlying UI
            DialogLayout(
                Modifier.semantics { dialog() },
                content
            )
        }
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

private class DialogLayout(
    context: Context,
    override val window: Window
) : FrameLayout(context), DialogWindowProvider

private class DialogWrapper(
    private val composeView: View,
    lightTheme: Boolean
) : Dialog(
    composeView.context,
    if (lightTheme) R.style.AppTheme_Light_FullScreenDialog else R.style.AppTheme_Dark_FullScreenDialog) {
    lateinit var onCloseRequest: () -> Unit

    private val dialogLayout: DialogLayout
    private var composition: Composition? = null

    init {
        window!!.requestFeature(Window.FEATURE_NO_TITLE)
        window!!.setBackgroundDrawableResource(android.R.color.transparent)
        window!!.setWindowAnimations(R.style.AppTheme_Slide)
        dialogLayout = DialogLayout(context, window!!)
        setContentView(dialogLayout)
        ViewTreeLifecycleOwner.set(dialogLayout, ViewTreeLifecycleOwner.get(composeView))
        ViewTreeViewModelStoreOwner.set(dialogLayout, ViewTreeViewModelStoreOwner.get(composeView))
        ViewTreeSavedStateRegistryOwner.set(
            dialogLayout,
            ViewTreeSavedStateRegistryOwner.get(composeView)
        )
    }

    // TODO(b/159900354): Make the Android Dialog full screen and the scrim fully transparent

    fun setContent(parentComposition: CompositionReference, children: @Composable () -> Unit) {
        // TODO: This should probably create a child composition of the original
        composition = dialogLayout.setContent(parentComposition, children)
    }

    private fun setSecureFlagEnabled(secureFlagEnabled: Boolean) {
        window!!.setFlags(
            if (secureFlagEnabled) {
                WindowManager.LayoutParams.FLAG_SECURE
            } else {
                WindowManager.LayoutParams.FLAG_SECURE.inv()
            },
            WindowManager.LayoutParams.FLAG_SECURE)
    }

    fun setProperties(properties: DialogProperties?) {
        if (properties != null && properties is AndroidDialogProperties) {
            setSecureFlagEnabled(properties.securePolicy
                .shouldApplySecureFlag(composeView.isFlagSecureEnabled()))
        } else {
            setSecureFlagEnabled(composeView.isFlagSecureEnabled())
        }
    }

    fun disposeComposition() {
        composition?.dispose()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val result = super.onTouchEvent(event)
        if (result) {
            onCloseRequest()
        }

        return result
    }

    override fun cancel() {
        // Prevents the dialog from dismissing itself
        return
    }

    override fun onBackPressed() {
        onCloseRequest()
    }
}

@Composable
private fun DialogLayout(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val placeables = measurables.map { it.measure(constraints) }
        val width = placeables.maxByOrNull { it.width }?.width ?: constraints.minWidth
        val height = placeables.maxByOrNull { it.height }?.height ?: constraints.minHeight
        layout(width, height) {
            placeables.forEach { it.placeRelative(0, 0) }
        }
    }
}

private fun View.isFlagSecureEnabled(): Boolean {
    val windowParams = rootView.layoutParams as? WindowManager.LayoutParams
    if (windowParams != null) {
        return (windowParams.flags and WindowManager.LayoutParams.FLAG_SECURE) != 0
    }
    return false
}

private fun SecureFlagPolicy.shouldApplySecureFlag(isSecureFlagSetOnParent: Boolean): Boolean {
    return when (this) {
        SecureFlagPolicy.SecureOff -> false
        SecureFlagPolicy.SecureOn -> true
        SecureFlagPolicy.Inherit -> isSecureFlagSetOnParent
    }
}