package cz.frantisekmasa.wfrp_master.common.character.trappings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.Money
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.inputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.SaveAction
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.SubheadBar
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun TransactionDialog(
    balance: Money,
    updateBalance: suspend (Money) -> Unit,
    onDismissRequest: () -> Unit,
) {
    FullScreenDialog(onDismissRequest = onDismissRequest) {
        val crowns = inputValue("", Rules.ifNotBlank(Rules.PositiveInteger()))
        val shillings = inputValue("", Rules.ifNotBlank(Rules.PositiveInteger()))
        val pennies = inputValue("", Rules.ifNotBlank(Rules.PositiveInteger()))
        var operation by rememberSaveable { mutableStateOf(Operation.ADD) }

        var validate by remember { mutableStateOf(false) }
        var errorMessage: String? by remember { mutableStateOf(null) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(Str.trappings_money_title_new_transaction)) },
                    navigationIcon = { CloseButton(onClick = onDismissRequest) },
                    actions = {
                        val coroutineScope = rememberCoroutineScope()
                        val notEnoughMoneyMessage =
                            stringResource(
                                Str.trappings_money_messages_not_enough_money,
                            )
                        var saving by remember { mutableStateOf(false) }

                        SaveAction(
                            enabled = !saving,
                            onClick = {
                                if (!listOf(crowns, shillings, pennies).all { it.isValid() }) {
                                    validate = true
                                }

                                val money =
                                    Money.sum(
                                        Money.crowns(crowns.toIntValue()),
                                        Money.shillings(shillings.toIntValue()),
                                        Money.pennies(pennies.toIntValue()),
                                    )

                                if (money.isZero()) {
                                    onDismissRequest()
                                    return@SaveAction
                                }

                                saving = true
                                errorMessage = null
                                coroutineScope.launch(Dispatchers.IO) {
                                    when (operation) {
                                        Operation.ADD -> updateBalance(balance + money)
                                        Operation.SUBTRACT -> {
                                            if (money > balance) {
                                                saving = false
                                                errorMessage = notEnoughMoneyMessage
                                            } else {
                                                updateBalance(balance - money)
                                            }
                                        }
                                    }
                                }
                            },
                        )
                    },
                )
            },
        ) {
            Column {
                SubheadBar(
                    Modifier.clickable {
                        crowns.value = balance.getCrowns().toInputValue()
                        shillings.value = balance.getShillings().toInputValue()
                        pennies.value = balance.getPennies().toInputValue()
                    },
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.small)) {
                        Text(
                            buildString {
                                append(stringResource(Str.trappings_money_balance))
                                append(':')
                            },
                            fontWeight = FontWeight.SemiBold,
                        )
                        MoneyBalance(balance)
                    }
                }

                Column(
                    Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(Spacing.bodyPadding),
                    verticalArrangement = Arrangement.spacedBy(Spacing.small),
                ) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.spacedBy(
                                Spacing.large,
                                Alignment.CenterHorizontally,
                            ),
                    ) {
                        RadioButtonWithText(
                            selected = operation == Operation.ADD,
                            onClick = { operation = Operation.ADD },
                            text = stringResource(Str.trappings_money_add),
                        )

                        RadioButtonWithText(
                            selected = operation == Operation.SUBTRACT,
                            onClick = { operation = Operation.SUBTRACT },
                            text = stringResource(Str.trappings_money_subtract),
                        )
                    }

                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
                    ) {
                        CoinInput(
                            crowns,
                            stringResource(Str.trappings_money_crowns),
                            validate,
                        )
                        CoinInput(
                            shillings,
                            stringResource(Str.trappings_money_shillings),
                            validate,
                        )
                        CoinInput(
                            pennies,
                            stringResource(Str.trappings_money_pennies),
                            validate,
                        )
                    }

                    errorMessage?.let {
                        Text(
                            it,
                            Modifier.fillMaxWidth(),
                            color = MaterialTheme.colors.error,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.CoinInput(
    value: InputValue,
    label: String,
    validate: Boolean,
) {
    TextInput(
        modifier = Modifier.weight(1f),
        label = label,
        value = value,
        validate = validate,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        maxLength = 4,
        showCharacterCount = false,
        placeholder = "0",
    )
}

private fun Int.toInputValue(): String = if (this == 0) "" else toString()

private fun InputValue.toIntValue(): Int = value.toIntOrNull() ?: 0

@Composable
private fun RadioButtonWithText(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(selected = selected, onClick = onClick)
        Text(text)
    }
}

private enum class Operation {
    SUBTRACT,
    ADD,
}
