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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.auth.JvmAuthenticationManager
import cz.frantisekmasa.wfrp_master.common.auth.JvmAuthenticationManager.RegistrationResult
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.CallbackRule
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.inputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.LocalPersistentSnackbarHolder
import cz.frantisekmasa.wfrp_master.common.core.utils.launchLogged
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.Dispatchers

@Composable
fun RegistrationDialog(
    auth: JvmAuthenticationManager,
    onDismissRequest: () -> Unit,
) {
    val email =
        inputValue(
            "",
            CallbackRule(
                stringResource(Str.authentication_messages_invalid_email),
            ) { EMAIL_REGEX.matches(it) },
        )
    val password =
        inputValue(
            "",
            Rules(
                Rules.NotBlank(),
                CallbackRule(stringResource(Str.authentication_messages_password_too_short, PASSWORD_MIN_LENGTH)) {
                    it.length >= PASSWORD_MIN_LENGTH
                },
            ),
        )
    var validate by remember { mutableStateOf(false) }
    var processing by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(Str.authentication_button_register)) },
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
                    TextInput(
                        label = stringResource(Str.authentication_label_password),
                        value = password,
                        validate = validate,
                        visualTransformation = PasswordVisualTransformation(),
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
            val errorEmailExists = stringResource(Str.authentication_messages_registration_email_exists)
            val errorUnknown = stringResource(Str.messages_error_unknown)
            val errorWeakPassword = stringResource(Str.authentication_messages_registration_weak_password)
            val errorTooManyAttempts = stringResource(Str.authentication_messages_too_many_attempts)
            val messageSuccess = stringResource(Str.authentication_messages_registration_success)

            TextButton(
                enabled = !processing,
                onClick = {
                    validate = true
                    if (email.isValid()) {
                        processing = true
                        coroutineScope.launchLogged(Dispatchers.IO) {
                            try {
                                when (val result = auth.register(email.value, password.value)) {
                                    RegistrationResult.Success -> {
                                        snackbarHolder.showSnackbar(
                                            messageSuccess,
                                            SnackbarDuration.Long,
                                        )
                                        onDismissRequest()
                                    }

                                    RegistrationResult.EmailExists -> {
                                        snackbarHolder.showSnackbar(errorEmailExists)
                                    }

                                    RegistrationResult.TooManyAttempts -> {
                                        snackbarHolder.showSnackbar(errorTooManyAttempts)
                                    }
                                    is RegistrationResult.WeakPassword -> {
                                        snackbarHolder.showSnackbar("$errorWeakPassword\n${result.criteria}")
                                    }
                                    RegistrationResult.UnknownError -> {
                                        snackbarHolder.showSnackbar(errorUnknown)
                                    }
                                }
                            } finally {
                                processing = false
                            }
                        }
                    }
                },
            ) {
                Text(stringResource(Str.authentication_button_register).uppercase())
            }
        },
    )
}
