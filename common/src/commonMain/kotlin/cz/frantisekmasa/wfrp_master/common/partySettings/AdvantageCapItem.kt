package cz.frantisekmasa.wfrp_master.common.partySettings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ListItem
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.Stats
import cz.frantisekmasa.wfrp_master.common.core.domain.party.settings.AdvantageCapExpression
import cz.frantisekmasa.wfrp_master.common.core.domain.party.settings.AdvantageSystem
import cz.frantisekmasa.wfrp_master.common.core.domain.party.settings.Settings
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.inputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.rule
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.SaveAction
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun AdvantageCapItem(settings: Settings, screenModel: PartySettingsScreenModel) {
    val title = stringResource(Str.combat_advantage_cap)

    if (settings.advantageSystem == AdvantageSystem.GROUP_ADVANTAGE) {
        ListItem(
            text = { Disabled { Text(title) } },
            secondaryText = {
                Disabled {
                    Text(stringResource(Str.combat_messages_does_not_apply_to_group_advantage))
                }
            }
        )

        return
    }

    var dialogVisible by remember { mutableStateOf(false) }
    val expression by derivedStateOf { settings.advantageCap }

    ListItem(
        text = { Text(title) },
        secondaryText = {
            if (expression.value == "") {
                Text(stringResource(Str.combat_advantage_unlimited), fontStyle = FontStyle.Italic)
            } else {
                Text(expression.value)
            }
        },
        modifier = Modifier.clickable { dialogVisible = true },
    )

    if (dialogVisible) {
        AdvantageCapDialog(
            currentExpression = expression,
            viewModel = screenModel,
            onDismissRequest = { dialogVisible = false },
        )
    }
}

@Composable
private inline fun Disabled(crossinline content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
        content()
    }
}

@Composable
fun AdvantageCapDialog(
    currentExpression: AdvantageCapExpression,
    viewModel: PartySettingsScreenModel,
    onDismissRequest: () -> Unit,
) {
    FullScreenDialog(onDismissRequest = onDismissRequest) {
        var validate by remember { mutableStateOf(false) }
        val newExpression = inputValue(
            currentExpression.value,
            rule(stringResource(Str.validation_invalid_expression), AdvantageCapExpression::isValid)
        )

        Scaffold(
            topBar = {
                val coroutineScope = rememberCoroutineScope()
                var saving by remember { mutableStateOf(false) }

                TopAppBar(
                    navigationIcon = { CloseButton(onClick = onDismissRequest) },
                    title = { Text(stringResource(Str.combat_advantage_cap)) },
                    actions = {
                        SaveAction(
                            enabled = !saving,
                            onClick = {
                                if (!newExpression.isValid()) {
                                    validate = true
                                    return@SaveAction
                                }

                                saving = true

                                coroutineScope.launch(Dispatchers.IO) {
                                    viewModel.updateSettings {
                                        it.copy(
                                            advantageCap = AdvantageCapExpression(
                                                newExpression.value
                                            )
                                        )
                                    }
                                    onDismissRequest()
                                }
                            }
                        )
                    }
                )
            }
        ) {
            Column(
                Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(Spacing.bodyPadding),
            ) {
                TextInput(
                    label = stringResource(Str.combat_advantage_cap),
                    value = newExpression,
                    validate = validate,
                    maxLength = AdvantageCapExpression.MAX_LENGTH,
                    helperText = stringResource(
                        Str.common_ui_expression_helper,
                        AdvantageVariables.joinToString(", "),
                    ),
                )
            }
        }
    }
}

private val AdvantageVariables = AdvantageCapExpression.constantsFrom(Stats.ZERO)
    .keys
    .sorted()
