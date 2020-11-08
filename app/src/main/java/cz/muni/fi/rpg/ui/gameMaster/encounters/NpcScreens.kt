package cz.muni.fi.rpg.ui.gameMaster.encounters

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.armour.Armor
import cz.muni.fi.rpg.model.domain.character.Stats
import cz.muni.fi.rpg.model.domain.encounter.Npc
import cz.muni.fi.rpg.model.domain.encounter.Wounds
import cz.muni.fi.rpg.ui.common.chunk
import cz.muni.fi.rpg.ui.common.composables.*
import cz.muni.fi.rpg.ui.router.Route
import cz.muni.fi.rpg.ui.router.Routing
import cz.muni.fi.rpg.viewModels.EncounterDetailViewModel
import kotlinx.coroutines.*
import org.koin.core.parameter.parametersOf

@Stable
private class CharacteristicsFormData(
    val weaponSkill: MutableState<String>,
    val ballisticSkill: MutableState<String>,
    val strength: MutableState<String>,
    val toughness: MutableState<String>,
    val initiative: MutableState<String>,
    val agility: MutableState<String>,
    val dexterity: MutableState<String>,
    val intelligence: MutableState<String>,
    val willPower: MutableState<String>,
    val fellowship: MutableState<String>,
) {
    companion object {
        @Composable
        fun fromCharacteristics(characteristics: Stats) = CharacteristicsFormData(
            weaponSkill = savedInstanceState { toNumericTextValue(characteristics.weaponSkill) },
            ballisticSkill = savedInstanceState { toNumericTextValue(characteristics.ballisticSkill) },
            strength = savedInstanceState { toNumericTextValue(characteristics.strength) },
            toughness = savedInstanceState { toNumericTextValue(characteristics.toughness) },
            initiative = savedInstanceState { toNumericTextValue(characteristics.initiative) },
            agility = savedInstanceState { toNumericTextValue(characteristics.agility) },
            dexterity = savedInstanceState { toNumericTextValue(characteristics.dexterity) },
            intelligence = savedInstanceState { toNumericTextValue(characteristics.intelligence) },
            willPower = savedInstanceState { toNumericTextValue(characteristics.willPower) },
            fellowship = savedInstanceState { toNumericTextValue(characteristics.fellowship) },
        )

        @Composable
        fun empty() = CharacteristicsFormData(
            weaponSkill = savedInstanceState { "" },
            ballisticSkill = savedInstanceState { "" },
            strength = savedInstanceState { "" },
            toughness = savedInstanceState { "" },
            initiative = savedInstanceState { "" },
            agility = savedInstanceState { "" },
            dexterity = savedInstanceState { "" },
            intelligence = savedInstanceState { "" },
            willPower = savedInstanceState { "" },
            fellowship = savedInstanceState { "" },
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
        toIntValue(weaponSkill.value) <= 100 &&
                toIntValue(ballisticSkill.value) <= 100 &&
                toIntValue(strength.value) <= 100 &&
                toIntValue(toughness.value) <= 100 &&
                toIntValue(initiative.value) <= 100 &&
                toIntValue(agility.value) <= 100 &&
                toIntValue(dexterity.value) <= 100 &&
                toIntValue(intelligence.value) <= 100 &&
                toIntValue(willPower.value) <= 100 &&
                toIntValue(fellowship.value) <= 100
}

@Stable
private class ArmorFormData(
    val head: MutableState<String>,
    val body: MutableState<String>,
    val shield: MutableState<String>,
    val leftArm: MutableState<String>,
    val rightArm: MutableState<String>,
    val leftLeg: MutableState<String>,
    val rightLeg: MutableState<String>,
) {
    companion object {
        @Composable
        fun fromArmor(armor: Armor) = ArmorFormData(
            head = savedInstanceState { toNumericTextValue(armor.head) },
            body = savedInstanceState { toNumericTextValue(armor.body) },
            shield = savedInstanceState { toNumericTextValue(armor.shield) },
            leftArm = savedInstanceState { toNumericTextValue(armor.leftArm) },
            rightArm = savedInstanceState { toNumericTextValue(armor.rightArm) },
            leftLeg = savedInstanceState { toNumericTextValue(armor.leftLeg) },
            rightLeg = savedInstanceState { toNumericTextValue(armor.rightLeg) },
        )

        @Composable
        fun empty() = ArmorFormData(
            head = savedInstanceState { "" },
            body = savedInstanceState { "" },
            shield = savedInstanceState { "" },
            leftArm = savedInstanceState { "" },
            rightArm = savedInstanceState { "" },
            leftLeg = savedInstanceState { "" },
            rightLeg = savedInstanceState { "" },
        )
    }

    fun toArmor() = Armor(
        head = toIntValue(head.value),
        body = toIntValue(body.value),
        shield = toIntValue(shield.value),
        leftArm = toIntValue(leftArm.value),
        rightArm = toIntValue(rightArm.value),
        leftLeg = toIntValue(leftLeg.value),
        rightLeg = toIntValue(rightLeg.value),
    )
}

@Stable
private class FormData(
    val name: MutableState<String>,
    val note: MutableState<String>,
    val wounds: MutableState<String>,
    val enemy: MutableState<Boolean>,
    val alive: MutableState<Boolean>,
    val characteristics: CharacteristicsFormData,
    val armor: ArmorFormData,
) {
    companion object {
        @Composable
        fun empty() = FormData(
            name = savedInstanceState { "" },
            note = savedInstanceState { "" },
            wounds = savedInstanceState { "" },
            enemy = savedInstanceState { true },
            alive = savedInstanceState { true },
            characteristics = CharacteristicsFormData.empty(),
            armor = ArmorFormData.empty(),
        )

        @Composable
        fun fromExistingNpc(npc: Npc) = FormData(
            name = savedInstanceState { npc.name },
            note = savedInstanceState { npc.note },
            wounds = savedInstanceState { npc.wounds.max.toString() },
            enemy = savedInstanceState { npc.enemy },
            alive = savedInstanceState { npc.alive },
            characteristics = CharacteristicsFormData.fromCharacteristics(npc.stats),
            armor = ArmorFormData.fromArmor(npc.armor),
        )
    }

    fun isValid() =
        characteristics.isValid() &&
                name.value.isNotBlank() &&
                toIntValue(wounds.value) > 0
}

@ExperimentalCoroutinesApi
@Composable
fun NpcDetailScreen(
    routing: Routing<Route.NpcDetail>,
) {
    val npcId = routing.route.npcId

    val coroutineScope = rememberCoroutineScope()
    val viewModel: EncounterDetailViewModel by viewModel { parametersOf(npcId.encounterId) }

    val npc = remember { viewModel.npcFlow(npcId) }.collectAsState().value
    val data = npc?.let { FormData.fromExistingNpc(it) }

    val validate = savedInstanceState { false }
    val submitEnabled = remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            NpcDetailTopBar(
                title = stringResource(R.string.title_npc_add),
                onSave = {
                    if (data == null) {
                        return@NpcDetailTopBar
                    }

                    if (!data.isValid()) {
                        validate.value = true
                    } else {
                        submitEnabled.value = false
                    }

                    coroutineScope.launch(Dispatchers.IO) {
                        viewModel.updateNpc(
                            id = npc.id,
                            name = data.name.value,
                            note = data.note.value,
                            maxWounds = data.wounds.value.toInt(),
                            stats = data.characteristics.toCharacteristics(),
                            armor = data.armor.toArmor(),
                            enemy = data.enemy.value,
                            alive = data.alive.value,
                            traits = emptyList(),
                            trappings = emptyList(),
                        )

                        withContext(Dispatchers.Main) { routing.backStack.pop() }
                    }
                },
                onBack = { routing.backStack.pop() },
                actionsEnabled = submitEnabled.value && data != null,
            )
        }
    ) {
        if (data == null) {
            Box(Modifier.fillMaxSize(), gravity = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            MainContainer(data, validate = validate.value)
        }
    }
}

@Composable
fun NpcCreationScreen(routing: Routing<Route.NpcCreation>) {
    val coroutineScope = rememberCoroutineScope()
    val viewModel: EncounterDetailViewModel by viewModel { parametersOf(routing.route.encounterId) }

    val data = FormData.empty()
    val validate = savedInstanceState { false }
    val submitEnabled = remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            NpcDetailTopBar(
                title = stringResource(R.string.title_npc_add),
                onSave = {
                    if (!data.isValid()) {
                        validate.value = true
                    } else {
                        submitEnabled.value = false
                        coroutineScope.launch(Dispatchers.IO) {
                            coroutineScope.launch {
                                viewModel.addNpc(
                                    name = data.name.value,
                                    note = data.note.value,
                                    wounds = Wounds.fromMax(data.wounds.value.toInt()),
                                    stats = data.characteristics.toCharacteristics(),
                                    armor = data.armor.toArmor(),
                                    enemy = data.enemy.value,
                                    alive = data.alive.value,
                                    traits = emptyList(),
                                    trappings = emptyList(),
                                )

                                withContext(Dispatchers.Main) { routing.backStack.pop() }
                            }
                        }
                    }
                },
                onBack = { routing.backStack.pop() },
                actionsEnabled = submitEnabled.value,
            )
        }
    ) {
        MainContainer(data, validate = validate.value)
    }
}

@Composable
private fun MainContainer(data: FormData, validate: Boolean) {
    ScrollableColumn {
        Column(Modifier.padding(24.dp).padding(bottom = 30.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(FormInputVerticalPadding)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(FormInputHorizontalPadding)
                ) {
                    TextInput(
                        modifier = Modifier.weight(0.7f),
                        label = stringResource(R.string.label_name),
                        value = data.name.value,
                        onValueChange = { data.name.value = it },
                        maxLength = Npc.NAME_MAX_LENGTH,
                        validate = validate,
                        rules = Rules(Rules.NotBlank()),
                    )

                    TextInput(
                        modifier = Modifier.weight(0.3f),
                        label = stringResource(R.string.label_wounds),
                        value = data.wounds.value,
                        onValueChange = { data.wounds.value = it },
                        keyboardType = KeyboardType.Number,
                        maxLength = 3,
                        validate = validate,
                        rules = Rules(
                            Rules.NotBlank(),
                            { v: String -> v.toInt() > 0 } to stringResource(R.string.error_value_is_0)
                        ),
                    )
                }

                TextInput(
                    label = stringResource(R.string.label_description),
                    value = data.note.value,
                    onValueChange = { data.note.value = it },
                    validate = validate,
                    multiLine = true,
                )

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    CheckboxWithText(
                        text = stringResource(R.string.label_enemy),
                        checked = data.enemy.value,
                        onCheckedChange = { data.enemy.value = it }
                    )
                    CheckboxWithText(
                        text = stringResource(R.string.label_alive),
                        checked = data.alive.value,
                        onCheckedChange = { data.alive.value = it }
                    )
                }
            }

            HorizontalLine()

            Text(
                stringResource(R.string.title_character_characteristics),
                style = MaterialTheme.typography.h6,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 20.dp, bottom = 16.dp).fillMaxWidth()
            )

            CharacteristicsSegment(data.characteristics, validate)

            HorizontalLine()

            Text(
                stringResource(R.string.title_armor),
                style = MaterialTheme.typography.h6,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 20.dp, bottom = 16.dp).fillMaxWidth()
            )

            ArmorSegment(data.armor, validate)
        }
    }
}

@Composable
private fun CharacteristicsSegment(data: CharacteristicsFormData, validate: Boolean) {
    val characteristics = listOf(
        R.string.label_shortcut_weapon_skill to data.weaponSkill,
        R.string.label_shortcut_ballistic_skill to data.ballisticSkill,
        R.string.label_shortcut_strength to data.strength,
        R.string.label_shortcut_toughness to data.toughness,
        R.string.label_shortcut_initiative to data.initiative,
        R.string.label_shortcut_agility to data.agility,
        R.string.label_shortcut_dexterity to data.dexterity,
        R.string.label_shortcut_intelligence to data.intelligence,
        R.string.label_shortcut_will_power to data.willPower,
        R.string.label_shortcut_fellowship to data.fellowship,
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        for (rowCharacteristics in characteristics.chunk(characteristics.size / 2)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                for ((label, value) in rowCharacteristics) {
                    TextInput(
                        label = stringResource(label),
                        value = value.value,
                        placeholder = "0",
                        keyboardType = KeyboardType.Number,
                        onValueChange = { value.value = it },
                        validate = validate,
                        maxLength = 3,
                        modifier = Modifier.weight(1f),
                        rules = Rules(
                            { v: String -> toIntValue(v) <= 100 } to stringResource(R.string.error_value_over_100)
                        )
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
            R.string.armor_head to data.head,
            R.string.armor_body to data.body,
            R.string.armor_shield to data.shield,
            0 to null // Empty container
        ),

        listOf(
            R.string.armor_left_arm to data.leftArm,
            R.string.armor_right_arm to data.rightArm,
            R.string.armor_left_leg to data.leftLeg,
            R.string.armor_right_leg to data.rightLeg,
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
                        label = stringResource(label),
                        value = value.value,
                        placeholder = "0",
                        keyboardType = KeyboardType.Number,
                        onValueChange = { value.value = it },
                        validate = validate,
                        maxLength = 3,
                        modifier = Modifier.weight(1f),
                        rules = Rules(
                            { v: String -> toIntValue(v) <= 100 } to stringResource(R.string.error_value_over_100)
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun NpcDetailTopBar(
    title: String,
    onSave: () -> Unit,
    onBack: () -> Unit,
    actionsEnabled: Boolean,
) {
    TopAppBar(
        navigationIcon = { BackButton(onBack) },
        title = { Text(title) },
        actions = {
            TopBarAction(onClick = onSave, enabled = actionsEnabled) {
                Text(stringResource(R.string.button_save).toUpperCase(Locale.current))
            }
        }
    )
}

private fun toNumericTextValue(value: Int) = if (value == 0) "" else value.toString()
private fun toIntValue(value: String) = value.toIntOrNull() ?: 0
