package cz.frantisekmasa.wfrp_master.desktop.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.AlertDialog
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.auth.AuthenticationManager
import cz.frantisekmasa.wfrp_master.common.auth.AuthenticationManager.PasswordResetResult
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.CallbackRule
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.inputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.LocalPersistentSnackbarHolder
import cz.frantisekmasa.wfrp_master.common.core.utils.launchLogged
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.Dispatchers

@Composable
fun ResetPasswordDialog(
    auth: AuthenticationManager,
    onDismissRequest: () -> Unit,
) {
    val email = inputValue(
        "",
        CallbackRule(
            stringResource(Str.authentication_messages_invalid_email)
        ) { EMAIL_REGEX.matches(it) }
    )

    var validate by remember { mutableStateOf(false) }
    var processing by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(Str.authentication_button_reset_password)) },
        text = {
            Column(Modifier.width(400.dp)) {
                if (processing) {
                    LinearProgressIndicator(Modifier.fillMaxWidth())
                } else {
                    TextInput(
                        label = stringResource(Str.authentication_label_email),
                        value = email,
                        validate = validate,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(Str.common_ui_button_dismiss).uppercase())
            }
        },
        confirmButton = {
            val coroutineScope = rememberCoroutineScope()
            val snackbarHolder = LocalPersistentSnackbarHolder.current
            val errorEmailNotFound = stringResource(Str.authentication_messages_email_not_found)
            val errorUnknown = stringResource(Str.messages_error_unknown)
            val messageEmailSent = stringResource(Str.authentication_messages_reset_password_email_sent)

            TextButton(
                enabled = !processing,
                onClick = {
                    validate = true
                    if (email.isValid()) {
                        processing = true
                        coroutineScope.launchLogged(Dispatchers.IO) {
                            try {
                                when (auth.resetPassword(email.value)) {
                                    PasswordResetResult.Success -> {
                                        snackbarHolder.showSnackbar(
                                            messageEmailSent,
                                            SnackbarDuration.Long,
                                        )
                                        onDismissRequest()
                                    }
                                    PasswordResetResult.EmailNotFound -> {
                                        snackbarHolder.showSnackbar(errorEmailNotFound)
                                    }
                                    PasswordResetResult.UnknownError -> {
                                        snackbarHolder.showSnackbar(errorUnknown)
                                    }
                                }
                            } finally {
                                processing = false
                            }
                        }
                    }
                }
            ) {
                Text(stringResource(Str.authentication_button_send).uppercase())
            }
        }
    )
}

private val EMAIL_REGEX = Regex("^[a-zA-Z0-9_!#\$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+\$")
