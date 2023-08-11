package cz.frantisekmasa.wfrp_master.common.shell

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.VisualOnlyIconDescription
import cz.frantisekmasa.wfrp_master.common.network.LocalConnectivityChecker
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun NetworkStatusBanner() {
    val isOnline by LocalConnectivityChecker.current.availability.collectWithLifecycle(true)

    Surface(Modifier.animateContentSize()) {
        if (isOnline) {
            return@Surface
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(Spacing.small),
            horizontalArrangement = Arrangement.spacedBy(Spacing.small, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Rounded.CloudOff,
                VisualOnlyIconDescription,
                Modifier.size(18.dp),
            )
            Text(stringResource(Str.messages_no_internet))
        }
    }
}
