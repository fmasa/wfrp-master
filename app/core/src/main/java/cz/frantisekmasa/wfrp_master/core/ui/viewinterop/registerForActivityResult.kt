package cz.frantisekmasa.wfrp_master.core.ui.viewinterop

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.commitNow

// TODO: Add abstraction that allows surviving activity death

@Deprecated("Use explicit keys",
    ReplaceWith("registerForActivityResult(keys = listOf(contract), contract = contract, callback = callback)")
)
@Composable
fun <I, O> registerForActivityResult(
    contract: ActivityResultContract<I, O>?,
    callback: ActivityResultCallback<O>
): Lazy<ActivityResultLauncher<I>> = registerForActivityResult(
    keys = listOf(contract),
    contract = contract,
    callback = callback
)
@Composable
fun <I, O> registerForActivityResult(
    keys: Any?,
    contract: ActivityResultContract<I, O>?,
    callback: ActivityResultCallback<O>
): Lazy<ActivityResultLauncher<I>> {
    val fragmentManager = fragmentManager()

    val handler = remember(keys, fragmentManager) {
        ActivityResultHandler(contract, callback)
            .also { fragmentManager.commitNow { add(it, null) } }
    }

    DisposableEffect(handler) {
        onDispose {
            if (fragmentManager.isStateSaved) {
                return@onDispose
            }

            fragmentManager.commitNow {
                remove(handler)
            }
        }
    }

    return lazy { handler.getLauncher() }
}

data class IntentResult(
    val resultCode: Int,
    val intent: Intent?,
)

/**
 * @internal This would be private if Fragments didn't have to public
 */
class ActivityResultHandler<I, O>(
    private val contract: ActivityResultContract<I, O>?,
    private val callback: ActivityResultCallback<O>
) : Fragment() {

    constructor() : this(null, {})

    private lateinit var launcher: ActivityResultLauncher<I>

    override fun onAttach(context: Context) {
        super.onAttach(context)

        contract?.let { launcher = registerForActivityResult(it, callback) }
    }

    fun getLauncher() = launcher
}