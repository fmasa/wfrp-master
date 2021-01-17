package cz.muni.fi.rpg.ui.character.inventory

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import cz.muni.fi.rpg.R
import cz.frantisekmasa.wfrp_master.core.domain.character.NotEnoughMoney
import cz.frantisekmasa.wfrp_master.core.domain.Money
import cz.frantisekmasa.wfrp_master.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.SaveAction
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.SubheadBar
import cz.muni.fi.rpg.viewModels.InventoryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun TransactionDialog(
    balance: Money,
    viewModel: InventoryViewModel,
    onDismissRequest: () -> Unit,
) {
    FullScreenDialog(onDismissRequest = onDismissRequest) {
        var crowns by savedInstanceState { "" }
        var shillings by savedInstanceState { "" }
        var pennies by savedInstanceState { "" }
        var operation by savedInstanceState { Operation.ADD }

        var errorMessage: String? by remember { mutableStateOf(null) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.title_transaction)) },
                    navigationIcon = { CloseButton(onClick = onDismissRequest) },
                    actions = {
                        val coroutineScope = rememberCoroutineScope()
                        val notEnoughMoneyMessage = stringResource(R.string.not_enough_money)
                        var saving by remember { mutableStateOf(false) }

                        SaveAction(
                            enabled = !saving,
                            onClick = {
                                val money = Money.sum(
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
                                    try {
                                        when (operation) {
                                            Operation.ADD -> viewModel.addMoney(money)
                                            Operation.SUBTRACT -> viewModel.subtractMoney(money)
                                        }
                                        withContext(Dispatchers.Main) {
                                            onDismissRequest()
                                        }
                                    } catch (e: NotEnoughMoney) {
                                        saving = false
                                        errorMessage = notEnoughMoneyMessage
                                    }
                                }
                            },
                        )
                    }
                )
            }
        ) {
            Column {
                SubheadBar(
                    Modifier.clickable {
                        crowns = balance.getCrowns().toInputValue()
                        shillings = balance.getShillings().toInputValue()
                        pennies = balance.getPennies().toInputValue()
                    }
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.small)) {
                        Text(stringResource(R.string.money_balance) + ":", fontWeight = FontWeight.SemiBold)
                        MoneyBalance(balance)
                    }
                }

                ScrollableColumn(
                    contentPadding = PaddingValues(Spacing.bodyPadding),
                    verticalArrangement = Arrangement.spacedBy(Spacing.small)
                ) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(
                            Spacing.medium,
                            Alignment.CenterHorizontally
                        )
                    ) {
                        RadioButtonWithText(
                            selected = operation == Operation.ADD,
                            onClick = { operation = Operation.ADD },
                            text = stringResource(R.string.money_add),
                        )

                        RadioButtonWithText(
                            selected = operation == Operation.SUBTRACT,
                            onClick = { operation = Operation.SUBTRACT },
                            text = stringResource(R.string.money_subtract),
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.medium)) {
                        TextInput(
                            modifier = Modifier.weight(1f),
                            label = stringResource(R.string.label_crowns),
                            value = crowns,
                            onValueChange = { crowns = it },
                            validate = false,
                            keyboardType = KeyboardType.Number,
                            maxLength = 4,
                            placeholder = "0",
                        )

                        TextInput(
                            modifier = Modifier.weight(1f),
                            label = stringResource(R.string.label_shillings),
                            value = shillings,
                            onValueChange = { shillings = it },
                            validate = false,
                            keyboardType = KeyboardType.Number,
                            maxLength = 4,
                            placeholder = "0",
                        )

                        TextInput(
                            modifier = Modifier.weight(1f),
                            label = stringResource(R.string.label_pennies),
                            value = pennies,
                            onValueChange = { pennies = it },
                            validate = false,
                            keyboardType = KeyboardType.Number,
                            maxLength = 4,
                            placeholder = "0",
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

private fun Int.toInputValue(): String = if (this == 0) "" else toString()

private fun String.toIntValue(): Int = toIntOrNull() ?: 0

@Composable
private fun RadioButtonWithText(text: String, selected: Boolean, onClick: () -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.tiny)) {
        RadioButton(selected = selected, onClick = onClick)
        Text(text)
    }
}

private enum class Operation {
    SUBTRACT,
    ADD,
}
