package cz.frantisekmasa.wfrp_master.core.ui.shell

import androidx.compose.runtime.*
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import cz.frantisekmasa.wfrp_master.core.di.AmbientKoinScope
import cz.frantisekmasa.wfrp_master.core.di.KoinScopeType
import org.koin.core.context.GlobalContext
import org.koin.core.qualifier.named
import timber.log.Timber

/**
 * Creates Scope for given screen.
 * When new scope is created (because of new composition with different [scopeOwner],
 * the old one is automatically destroyed.
 * This means that [scopeOwner] for same screen *MUST* be same between recompositions.
 */
@Composable
fun <T> KoinScope(scopeOwner: T, content: @Composable () -> Unit) {
    val koinContext = GlobalContext.get()

    var oldScopeId: String? by savedInstanceState { null }
    val currentScopeId = "screen${scopeOwner.hashCode()}"

    onCommit {
        if (oldScopeId != currentScopeId) {
            oldScopeId
                ?.let { koinContext.getScopeOrNull(it) }
                ?.apply {
                    close()
                    Timber.d("Closing scope $oldScopeId")
                }

            oldScopeId = currentScopeId
        }
    }

    val scope = koinContext.getOrCreateScope(currentScopeId, named(KoinScopeType.Screen))

    Providers(
        AmbientKoinScope provides scope,
        content = content,
    )
}
