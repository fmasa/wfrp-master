package cz.frantisekmasa.wfrp_master.common.core.tips

import cz.frantisekmasa.wfrp_master.common.core.shared.SettingsStorage
import cz.frantisekmasa.wfrp_master.common.core.shared.stringSetKey
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.UserTip
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DismissedUserTipsHolder(
    private val settingsStorage: SettingsStorage,
) {
    val dismissedTips: Flow<Set<UserTip>> = settingsStorage.watch(DISMISSED_TIPS)
        .map { dismissedTips ->
            dismissedTips
                ?.mapNotNull { UserTip.values().firstOrNull { tip -> tip.name == it } }
                ?.toSet() ?: emptySet()
        }

    suspend fun dismissTip(tip: UserTip) {
        settingsStorage.edit(DISMISSED_TIPS) { dismissedTips ->
            (dismissedTips ?: emptySet()) + tip.name
        }
    }

    companion object {
        /**
         * Intentionally using non-typesafe Set<String> instead of Set<UserTip> as it allows
         * us to remove tips in future without breaking settings deserialization.
         */
        private val DISMISSED_TIPS = stringSetKey("dismissed_tips")
    }
}
