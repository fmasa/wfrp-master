package cz.frantisekmasa.wfrp_master.common.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.auth.JvmAuthenticationManager
import cz.frantisekmasa.wfrp_master.common.core.auth.LocalUser
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CardButton
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardContainer
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardTitle
import cz.frantisekmasa.wfrp_master.common.core.ui.text.SingleLineTextValue
import cz.frantisekmasa.wfrp_master.common.core.utils.launchLogged
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.Dispatchers
import org.kodein.di.compose.localDI
import org.kodein.di.instance

@Composable
actual fun SignInCard(settingsScreenModel: SettingsScreenModel) {
    CardContainer(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CardTitle(stringResource(Str.settings_title_account))

            val email = LocalUser.current.email

            if (email != null) {
                SingleLineTextValue(stringResource(Str.authentication_label_email), email)
            }

            val auth: JvmAuthenticationManager by localDI().instance()
            val coroutineScope = rememberCoroutineScope()

            CardButton(
                text = stringResource(Str.authentication_button_log_out),
                onClick = {
                    coroutineScope.launchLogged(Dispatchers.IO) {
                        auth.logout()
                    }
                }
            )
        }
    }
}
