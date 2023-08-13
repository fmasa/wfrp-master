package cz.frantisekmasa.wfrp_master.common.npcs.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.Characteristic
import cz.frantisekmasa.wfrp_master.common.core.domain.Stats
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Armour
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.CheckboxWithText
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.checkboxValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.inputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.HorizontalLine
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.encounters.domain.Npc
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun NpcForm(data: FormData, validate: Boolean) {
    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .padding(Spacing.bodyPadding)
            .padding(bottom = 30.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.small)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.large)
            ) {

                TextInput(
                    modifier = Modifier.weight(0.7f),
                    label = stringResource(Str.npcs_label_name),
                    value = data.name,
                    maxLength = Npc.NAME_MAX_LENGTH,
                    validate = validate,
                )

                TextInput(
                    modifier = Modifier.weight(0.3f),
                    label = stringResource(Str.points_wounds),
                    value = data.wounds,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    maxLength = 3,
                    validate = validate,
                )
            }

            TextInput(
                label = stringResource(Str.npcs_label_description),
                value = data.note,
                validate = validate,
                multiLine = true,
            )

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                CheckboxWithText(
                    text = stringResource(Str.npcs_label_enemy),
                    checked = data.enemy.value,
                    onCheckedChange = { data.enemy.value = it }
                )
                CheckboxWithText(
                    text = stringResource(Str.npcs_label_alive),
                    checked = data.alive.value,
                    onCheckedChange = { data.alive.value = it }
                )
            }
        }

        HorizontalLine()

        Text(
            stringResource(Str.npcs_title_characteristics),
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 20.dp, bottom = 16.dp)
                .fillMaxWidth()
        )

        CharacteristicsSegment(data.characteristics, validate)

        HorizontalLine()

        Text(
            stringResource(Str.npcs_title_armour),
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 20.dp, bottom = 16.dp)
                .fillMaxWidth()
        )

        ArmorSegment(data.armor, validate)
    }
}

@Composable
private fun CharacteristicsSegment(data: CharacteristicsFormData, validate: Boolean) {
    val characteristics = listOf(
        Characteristic.WEAPON_SKILL to data.weaponSkill,
        Characteristic.BALLISTIC_SKILL to data.ballisticSkill,
        Characteristic.STRENGTH to data.strength,
        Characteristic.TOUGHNESS to data.toughness,
        Characteristic.INITIATIVE to data.initiative,
        Characteristic.AGILITY to data.agility,
        Characteristic.DEXTERITY to data.dexterity,
        Characteristic.INTELLIGENCE to data.intelligence,
        Characteristic.WILL_POWER to data.willPower,
        Characteristic.FELLOWSHIP to data.fellowship,
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        for (rowCharacteristics in characteristics.chunked(characteristics.size / 2)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                for ((characteristic, value) in rowCharacteristics) {
                    TextInput(
                        label = characteristic.getShortcutName(),
                        value = value,
                        placeholder = "0",
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        validate = validate,
                        maxLength = 3,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun ArmorSegment(data: ArmorFormData, validate: Boolean) {
    val rows = listOf(
        listOf(
            stringResource(Str.combat_hit_locations_head) to data.head,
            stringResource(Str.combat_hit_locations_body) to data.body,
            stringResource(Str.armour_shield) to data.shield,
            "" to null // Empty container
        ),

        listOf(
            stringResource(Str.combat_hit_locations_left_arm) to data.leftArm,
            stringResource(Str.combat_hit_locations_right_arm) to data.rightArm,
            stringResource(Str.combat_hit_locations_left_leg) to data.leftLeg,
            stringResource(Str.combat_hit_locations_right_leg) to data.rightLeg,
        )
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        for (rowParts in rows) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                for ((label, value) in rowParts) {
                    if (value == null) {
                        Spacer(Modifier.weight(1f))
                        continue
                    }

                    TextInput(
                        label = label,
                        value = value,
                        placeholder = "0",
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        validate = validate,
                        maxLength = 3,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Stable
class CharacteristicsFormData(
    val weaponSkill: InputValue,
    val ballisticSkill: InputValue,
    val strength: InputValue,
    val toughness: InputValue,
    val initiative: InputValue,
    val agility: InputValue,
    val dexterity: InputValue,
    val intelligence: InputValue,
    val willPower: InputValue,
    val fellowship: InputValue,
) {
    companion object {
        @Composable
        fun fromCharacteristics(characteristics: Stats?) = CharacteristicsFormData(
            weaponSkill = characteristicValue(characteristics?.weaponSkill),
            ballisticSkill = characteristicValue(characteristics?.ballisticSkill),
            strength = characteristicValue(characteristics?.strength),
            toughness = characteristicValue(characteristics?.toughness),
            initiative = characteristicValue(characteristics?.initiative),
            agility = characteristicValue(characteristics?.agility),
            dexterity = characteristicValue(characteristics?.dexterity),
            intelligence = characteristicValue(characteristics?.intelligence),
            willPower = characteristicValue(characteristics?.willPower),
            fellowship = characteristicValue(characteristics?.fellowship),
        )

        @Composable
        private fun characteristicValue(value: Int?): InputValue =
            inputValue(
                when (value) {
                    null, 0 -> ""
                    else -> value.toString()
                },
                // We use rule without message, because text these inputs are too small to show error messages
                Rules.withEmptyMessage {
                    it.isBlank() || (it.toIntOrNull() != null && it.toInt() in 0..100)
                },
            )
    }

    fun toCharacteristics() = Stats(
        weaponSkill = weaponSkill.value.toIntOrNull() ?: 0,
        ballisticSkill = ballisticSkill.value.toIntOrNull() ?: 0,
        strength = strength.value.toIntOrNull() ?: 0,
        toughness = toughness.value.toIntOrNull() ?: 0,
        initiative = initiative.value.toIntOrNull() ?: 0,
        agility = agility.value.toIntOrNull() ?: 0,
        dexterity = dexterity.value.toIntOrNull() ?: 0,
        intelligence = intelligence.value.toIntOrNull() ?: 0,
        willPower = willPower.value.toIntOrNull() ?: 0,
        fellowship = fellowship.value.toIntOrNull() ?: 0,
    )

    fun isValid() =
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
        ).all { it.isValid() }
}

@Stable
class ArmorFormData(
    val head: InputValue,
    val body: InputValue,
    val shield: InputValue,
    val leftArm: InputValue,
    val rightArm: InputValue,
    val leftLeg: InputValue,
    val rightLeg: InputValue,
) {
    companion object {
        @Composable
        fun fromArmor(armor: Armour?) = ArmorFormData(
            head = armorValue(armor?.head),
            body = armorValue(armor?.body),
            shield = armorValue(armor?.shield),
            leftArm = armorValue(armor?.leftArm),
            rightArm = armorValue(armor?.rightArm),
            leftLeg = armorValue(armor?.leftLeg),
            rightLeg = armorValue(armor?.rightLeg),
        )

        @Composable
        private fun armorValue(default: Int?) = inputValue(
            when (default) {
                0, null -> ""
                else -> default.toString()
            },
            // We use rule without message, because text these inputs are too small to show error messages
            Rules.withEmptyMessage { it.isBlank() || (it.toIntOrNull() != null && it.toInt() in 1..100) }
        )
    }

    fun toArmor() = Armour(
        body = body.value.toIntOrNull() ?: 0,
        shield = shield.value.toIntOrNull() ?: 0,
        leftArm = leftArm.value.toIntOrNull() ?: 0,
        rightArm = rightArm.value.toIntOrNull() ?: 0,
        leftLeg = leftLeg.value.toIntOrNull() ?: 0,
        rightLeg = rightLeg.value.toIntOrNull() ?: 0,
        head = head.value.toIntOrNull() ?: 0,
    )

    fun isValid(): Boolean =
        listOf(body, shield, leftArm, rightArm, leftLeg, rightLeg, head).all { it.isValid() }
}

@Stable
class FormData(
    val name: InputValue,
    val note: InputValue,
    val wounds: InputValue,
    val enemy: MutableState<Boolean>,
    val alive: MutableState<Boolean>,
    val characteristics: CharacteristicsFormData,
    val armor: ArmorFormData,
) {
    companion object {
        @Composable
        fun empty() = fromNpc(null)

        @Composable
        fun fromExistingNpc(npc: Npc) = fromNpc(npc)

        @Composable
        private fun fromNpc(npc: Npc?) = FormData(
            name = inputValue(npc?.name ?: "", Rules.NotBlank()),
            note = inputValue(npc?.note ?: ""),
            wounds = inputValue(npc?.wounds?.max?.toString() ?: "", Rules.PositiveInteger()),
            enemy = checkboxValue(npc?.enemy ?: true),
            alive = checkboxValue(npc?.alive ?: true),
            characteristics = CharacteristicsFormData.fromCharacteristics(npc?.stats),
            armor = ArmorFormData.fromArmor(npc?.armor),
        )
    }

    fun isValid() =
        characteristics.isValid() && armor.isValid() && name.isValid() && wounds.isValid()
}
