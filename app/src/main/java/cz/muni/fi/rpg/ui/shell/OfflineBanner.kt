package cz.muni.fi.rpg.ui.shell

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.core.ui.primitives.Spacing
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.viewModels.provideNetworkViewModel

@Composable
fun NetworkStatusBanner() {
    val isOnline = provideNetworkViewModel().isConnectedToInternet.observeAsState().value ?: true

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
            Icon(vectorResource(R.drawable.ic_offline), Modifier.size(18.dp))
            Text(stringResource(R.string.no_internet))
        }
    }
}
