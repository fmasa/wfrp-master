package cz.muni.fi.rpg.ui.shell

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.core.ui.primitives.VisualOnlyIconDescription
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.viewModels.provideNetworkViewModel

@Composable
fun NetworkStatusBanner() {
    val isOnline by provideNetworkViewModel().isConnectedToInternet.collectWithLifecycle(true)

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
                painterResource(R.drawable.ic_offline),
                VisualOnlyIconDescription,
                Modifier.size(18.dp),
            )
            Text(stringResource(R.string.no_internet))
        }
    }
}
