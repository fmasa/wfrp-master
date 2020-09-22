package cz.muni.fi.rpg.ui.characterCreation

import androidx.annotation.StringRes
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.Stats
import cz.muni.fi.rpg.ui.common.composables.FormData
import cz.muni.fi.rpg.ui.common.composables.TextInput

object CharacterCharacteristicsForm {
    @Stable
    class Data(
        val weaponSkill: MutableState<Pair<String, String>>,
        val ballisticSkill: MutableState<Pair<String, String>>,
        val strength: MutableState<Pair<String, String>>,
        val toughness: MutableState<Pair<String, String>>,
        val initiative: MutableState<Pair<String, String>>,
        val agility: MutableState<Pair<String, String>>,
        val dexterity: MutableState<Pair<String, String>>,
        val intelligence: MutableState<Pair<String, String>>,
        val willPower: MutableState<Pair<String, String>>,
        val fellowship: MutableState<Pair<String, String>>,
    ) : FormData {
        companion object {
            @Composable
            fun empty() = Data(
                weaponSkill = savedInstanceState { "" to "" },
                ballisticSkill = savedInstanceState { "" to "" },
                strength = savedInstanceState { "" to "" },
                toughness = savedInstanceState { "" to "" },
                initiative = savedInstanceState { "" to "" },
                agility = savedInstanceState { "" to "" },
                dexterity = savedInstanceState { "" to "" },
                intelligence = savedInstanceState { "" to "" },
                willPower = savedInstanceState { "" to "" },
                fellowship = savedInstanceState { "" to "" },
            )
        }

        // There is no need for validation as it's ensured by keyboard
        override fun isValid() = true

        fun toBaseCharacteristics(): Stats = Stats(
            weaponSkill = toValue(weaponSkill.value.first),
            ballisticSkill = toValue(ballisticSkill.value.first),
            strength = toValue(strength.value.first),
            toughness = toValue(toughness.value.first),
            initiative = toValue(initiative.value.first),
            agility = toValue(agility.value.first),
            dexterity = toValue(dexterity.value.first),
            intelligence = toValue(intelligence.value.first),
            willPower = toValue(willPower.value.first),
            fellowship = toValue(fellowship.value.first),
        )

        fun toCharacteristicAdvances(): Stats = Stats(
            weaponSkill = toValue(weaponSkill.value.second),
            ballisticSkill = toValue(ballisticSkill.value.second),
            strength = toValue(strength.value.second),
            toughness = toValue(toughness.value.second),
            initiative = toValue(initiative.value.second),
            agility = toValue(agility.value.second),
            dexterity = toValue(dexterity.value.second),
            intelligence = toValue(intelligence.value.second),
            willPower = toValue(willPower.value.second),
            fellowship = toValue(fellowship.value.second),
        )

        private fun toValue(value: String) = value.toIntOrNull() ?: 0
    }
}

@ExperimentalLayout
@Composable
fun CharacterCharacteristicsForm(
    data: CharacterCharacteristicsForm.Data,
    validate: Boolean,
) {
    val characteristics = listOf(
        R.string.label_weapon_skill to data.weaponSkill,
        R.string.label_ballistic_skill to data.ballisticSkill,
        R.string.label_strength to data.strength,
        R.string.label_toughness to data.toughness,
        R.string.label_initiative to data.initiative,
        R.string.label_agility to data.agility,
        R.string.label_dexterity to data.dexterity,
        R.string.label_intelligence to data.intelligence,
        R.string.label_will_power to data.willPower,
        R.string.label_fellowship to data.fellowship,
    )

    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        for (rowCharacteristics in characteristics.chunk(2)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                for ((labelRes, baseAndAdvances) in rowCharacteristics) {
                    CharacteristicInputs(
                        labelRes = labelRes,
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
    @StringRes labelRes: Int,
    validate: Boolean,
    modifier: Modifier,
    baseAndAdvances: MutableState<Pair<String, String>>
) {
    Column(modifier) {
        Text(stringResource(labelRes), Modifier.align(Alignment.CenterHorizontally))

        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            val base = baseAndAdvances.value.first
            val advances = baseAndAdvances.value.second

            TextInput(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.label_base),
                keyboardType = KeyboardType.Number,
                value = base,
                onValueChange = { baseAndAdvances.value = it to advances },
                horizontalAlignment = Alignment.CenterHorizontally,
                validate = validate,
                maxLength = 3,
                placeholder = "0",
            )

            TextInput(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.label_advances),
                keyboardType = KeyboardType.Number,
                value = advances,
                onValueChange = { baseAndAdvances.value = base to it },
                horizontalAlignment = Alignment.CenterHorizontally,
                validate = validate,
                maxLength = 3,
                placeholder = "0",
            )
        }
    }
}

private fun <T> List<T>.chunk(itemsInChunk: Int): List<List<T>> {
    val chunks = (0..(size / itemsInChunk)).map { ArrayList<T>(itemsInChunk) }

    for (index in indices) {
        chunks[index / itemsInChunk].add(this[index])
    }

    return chunks
}