package cz.frantisekmasa.wfrp_master.common.auth

import cz.frantisekmasa.wfrp_master.common.core.auth.User
import cz.frantisekmasa.wfrp_master.common.core.auth.UserId
import cz.frantisekmasa.wfrp_master.common.core.auth.UserProvider
import cz.frantisekmasa.wfrp_master.common.core.logging.Reporter
import dev.gitlive.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CommonAuthenticationManager(
    private val auth: FirebaseAuth,
    private val supportsEmail: Boolean,
) : UserProvider {
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    val user = auth.authStateChanged
        .map {
            it?.let {
                User(
                    id = UserId(it.uid),
                    email = if (supportsEmail) it.email else null,
                )
            }
        }
        .stateIn(
            coroutineScope,
            SharingStarted.Eagerly,
            auth.currentUser?.let {
                User(
                    id = UserId(it.uid),
                    email = if (supportsEmail) it.email else null,
                )
            }
        )

    val authenticated: StateFlow<Boolean?> = user
        .map { it != null }
        .stateIn(coroutineScope, SharingStarted.Eagerly, null)

    override val userId: UserId? get() = user.value?.id

    init {
        coroutineScope.launch {
            user.collect { it?.let { Reporter.setUserId(it.id) } }
        }
    }
}
