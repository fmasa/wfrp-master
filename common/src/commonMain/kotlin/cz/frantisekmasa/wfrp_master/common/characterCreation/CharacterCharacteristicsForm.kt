package cz.frantisekmasa.wfrp_master.common.characterCreation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.Stats
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.HydratedFormData
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.inputValue
import dev.icerock.moko.resources.compose.stringResource

object CharacterCharacteristicsForm {
    @Stable
    class Data(
        val weaponSkill: Pair<InputValue, InputValue>,
        val ballisticSkill: Pair<InputValue, InputValue>,
        val strength: Pair<InputValue, InputValue>,
        val toughness: Pair<InputValue, InputValue>,
        val initiative: Pair<InputValue, InputValue>,
        val agility: Pair<InputValue, InputValue>,
        val dexterity: Pair<InputValue, InputValue>,
        val intelligence: Pair<InputValue, InputValue>,
        val willPower: Pair<InputValue, InputValue>,
        val fellowship: Pair<InputValue, InputValue>,
    ) : HydratedFormData<Value> {
        companion object {
            @Composable
            fun fromCharacter(character: Character?): Data {
                val base = character?.characteristicsBase
                val advances = character?.characteristicsAdvances

                return Data(
                    weaponSkill = toPair(base, advances) { it.weaponSkill },
                    ballisticSkill = toPair(base, advances) { it.ballisticSkill },
                    strength = toPair(base, advances) { it.strength },
                    toughness = toPair(base, advances) { it.toughness },
                    initiative = toPair(base, advances) { it.initiative },
                    agility = toPair(base, advances) { it.agility },
                    dexterity = toPair(base, advances) { it.dexterity },
                    intelligence = toPair(base, advances) { it.intelligence },
                    willPower = toPair(base, advances) { it.willPower },
                    fellowship = toPair(base, advances) { it.fellowship },
                )
            }

            @Composable
            private fun toPair(
                base: Stats?,
                advances: Stats?,
                getValue: (Stats) -> Int,
            ) = Pair(
                characteristicValue(base?.let(getValue)),
                characteristicValue(advances?.let(getValue)),
            )

            @Composable
            private fun characteristicValue(defaultValue: Int?) =
                inputValue(
                    defaultValue?.toTextValue() ?: "",
                    // Inputs are too thin to show error message
                    Rules.withEmptyMessage(Rules.ifNotBlank(Rules.NonNegativeInteger())),
                )

            private fun Int.toTextValue() = if (this == 0) "" else this.toString()
        }

        override fun isValid() =
            listOf(
                weaponSkill,
                ballisticSkill,
                strength,
                toughness,
                initiative,
                agility,
                dexterity,
                intelligence,
                willPower,
                fellowship,
            ).all { (base, advances) -> base.isValid() && advances.isValid() }

        override fun toValue(): Value =
            Value(
                base =
                    Stats(
                        weaponSkill = toValue(weaponSkill.first.value),
                        ballisticSkill = toValue(ballisticSkill.first.value),
                        strength = toValue(strength.first.value),
                        toughness = toValue(toughness.first.value),
                        initiative = toValue(initiative.first.value),
                        agility = toValue(agility.first.value),
                        dexterity = toValue(dexterity.first.value),
                        intelligence = toValue(intelligence.first.value),
                        willPower = toValue(willPower.first.value),
                        fellowship = toValue(fellowship.first.value),
                    ),
                advances =
                    Stats(
                        weaponSkill = toValue(weaponSkill.second.value),
                        ballisticSkill = toValue(ballisticSkill.second.value),
                        strength = toValue(strength.second.value),
                        toughness = toValue(toughness.second.value),
                        initiative = toValue(initiative.second.value),
                        agility = toValue(agility.second.value),
                        dexterity = toValue(dexterity.second.value),
                        intelligence = toValue(intelligence.second.value),
                        willPower = toValue(willPower.second.value),
                        fellowship = toValue(fellowship.second.value),
                    ),
            )

        private fun toValue(value: String) = value.toIntOrNull() ?: 0
    }

    data class Value(
        val base: Stats,
        val advances: Stats,
    )
}

@Composable
fun CharacterCharacteristicsForm(
    data: CharacterCharacteristicsForm.Data,
    validate: Boolean,
) {
    val characteristics =
        listOf(
            stringResource(Str.characteristics_weapon_skill) to data.weaponSkill,
            stringResource(Str.characteristics_ballistic_skill) to data.ballisticSkill,
            stringResource(Str.characteristics_strength) to data.strength,
            stringResource(Str.characteristics_toughness) to data.toughness,
            stringResource(Str.characteristics_initiative) to data.initiative,
            stringResource(Str.characteristics_agility) to data.agility,
            stringResource(Str.characteristics_dexterity) to data.dexterity,
            stringResource(Str.characteristics_intelligence) to data.intelligence,
            stringResource(Str.characteristics_will_power) to data.willPower,
            stringResource(Str.characteristics_fellowship) to data.fellowship,
        )

    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        for (rowCharacteristics in characteristics.chunked(2)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                for ((label, baseAndAdvances) in rowCharacteristics) {
                    CharacteristicInputs(
                        label = label,
                        validate = validate,
                        modifier = Modifier.weight(1f),
                        baseAndAdvances = baseAndAdvances,
                    )
                }
            }
        }
    }
}

@Composable
private fun CharacteristicInputs(
    label: String,
    validate: Boolean,
    modifier: Modifier,
    baseAndAdvances: Pair<InputValue, InputValue>,
) {
    Column(modifier) {
        Text(label, Modifier.align(Alignment.CenterHorizontally))

        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            val base = baseAndAdvances.first
            val advances = baseAndAdvances.second

            val focusManager = LocalFocusManager.current
            val keyboardOptions =
                KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                )
            val keyboardActions =
                KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Next) },
                )

            TextInput(
                modifier = Modifier.weight(1f),
                label = stringResource(Str.character_label_characteristic_base),
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                value = base,
                horizontalAlignment = Alignment.CenterHorizontally,
                validate = validate,
                maxLength = 3,
                showCharacterCount = false,
                placeholder = "0",
            )

            TextInput(
                modifier = Modifier.weight(1f),
                label = stringResource(Str.character_label_characteristic_advances),
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                value = advances,
                horizontalAlignment = Alignment.CenterHorizontally,
                validate = validate,
                maxLength = 3,
                showCharacterCount = false,
                placeholder = "0",
            )
        }
    }
}
