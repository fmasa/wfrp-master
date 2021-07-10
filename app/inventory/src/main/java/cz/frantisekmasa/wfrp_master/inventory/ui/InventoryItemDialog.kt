package cz.frantisekmasa.wfrp_master.inventory.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import cz.frantisekmasa.wfrp_master.core.domain.NamedEnum
import cz.frantisekmasa.wfrp_master.core.ui.components.FormDialog
import cz.frantisekmasa.wfrp_master.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.core.ui.forms.CheckboxWithText
import cz.frantisekmasa.wfrp_master.core.ui.forms.ErrorMessage
import cz.frantisekmasa.wfrp_master.core.ui.forms.Filter
import cz.frantisekmasa.wfrp_master.core.ui.forms.HydratedFormData
import cz.frantisekmasa.wfrp_master.core.ui.forms.InputLabel
import cz.frantisekmasa.wfrp_master.core.ui.forms.InputValue
import cz.frantisekmasa.wfrp_master.core.ui.forms.Rule
import cz.frantisekmasa.wfrp_master.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.core.ui.forms.SelectBox
import cz.frantisekmasa.wfrp_master.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.core.ui.forms.inputValue
import cz.frantisekmasa.wfrp_master.core.ui.primitives.NumberPicker
import cz.frantisekmasa.wfrp_master.inventory.R
import cz.frantisekmasa.wfrp_master.inventory.domain.Encumbrance
import cz.frantisekmasa.wfrp_master.inventory.domain.InventoryItem
import cz.frantisekmasa.wfrp_master.inventory.domain.InventoryItemId
import cz.frantisekmasa.wfrp_master.inventory.domain.TrappingType
import cz.frantisekmasa.wfrp_master.inventory.domain.armour.ArmourLocation
import cz.frantisekmasa.wfrp_master.inventory.domain.armour.ArmourPoints
import cz.frantisekmasa.wfrp_master.inventory.domain.armour.ArmourType
import cz.frantisekmasa.wfrp_master.inventory.domain.weapon.AmmunitionRangeExpression
import cz.frantisekmasa.wfrp_master.inventory.domain.weapon.DamageExpression
import cz.frantisekmasa.wfrp_master.inventory.domain.weapon.MeleeWeaponGroup
import cz.frantisekmasa.wfrp_master.inventory.domain.weapon.RangedWeaponGroup
import cz.frantisekmasa.wfrp_master.inventory.domain.weapon.Reach
import cz.frantisekmasa.wfrp_master.inventory.domain.weapon.WeaponEquip
import cz.frantisekmasa.wfrp_master.inventory.domain.weapon.WeaponFlaw
import cz.frantisekmasa.wfrp_master.inventory.domain.weapon.WeaponQuality
import cz.frantisekmasa.wfrp_master.inventory.domain.weapon.WeaponRangeExpression
import java.util.UUID
import kotlin.math.max

@Composable
internal fun InventoryItemDialog(
    viewModel: InventoryViewModel,
    existingItem: InventoryItem?,
    onDismissRequest: () -> Unit,
) {
    FullScreenDialog(onDismissRequest = onDismissRequest) {
        val formData = InventoryItemFormData.fromItem(existingItem)

        FormDialog(
            title = if (existingItem != null)
                R.string.title_inventory_item_edit
            else R.string.title_inventory_item_add,
            onDismissRequest = onDismissRequest,
            formData = formData,
            onSave = viewModel::saveInventoryItem,
        ) { validate ->
            TextInput(
                label = stringResource(R.string.label_name),
                value = formData.name,
                validate = validate,
                maxLength = InventoryItem.NAME_MAX_LENGTH,
            )

            TextInput(
                label = stringResource(R.string.inventory_item_quantity),
                value = formData.quantity,
                validate = validate,
                keyboardType = KeyboardType.Number,
            )

            TextInput(
                label = stringResource(R.string.inventory_item_encumbrance),
                value = formData.encumbrance,
                maxLength = 8,
                validate = validate,
                keyboardType = KeyboardType.Number,
                filters = listOf(Filter.DigitsAndDotSymbolsOnly),
            )

            TextInput(
                label = stringResource(R.string.label_description),
                value = formData.description,
                validate = validate,
                maxLength = InventoryItem.DESCRIPTION_MAX_LENGTH,
            )

            TrappingTypeForm(
                formData = formData.type,
                validate = validate,
            )
        }
    }
}

@Composable
private fun TrappingTypeForm(formData: TrappingTypeFormData, validate: Boolean) {
    SelectBox(
        label = stringResource(R.string.label_trapping_type),
        value = formData.type.value,
        onValueChange = { formData.type.value = it },
        items = remember { TrappingTypeOption.values() },
    )

    @Suppress("UNUSED_VARIABLE")
    val exhaustive = when (formData.type.value) {
        TrappingTypeOption.AMMUNITION -> {
            TextInput(
                label = stringResource(R.string.label_range),
                value = formData.ammunitionRange,
                validate = validate,
                helperText = stringResource(R.string.helper_ammunition_range),
            )
            CheckboxList(
                items = RangedWeaponGroup.values(),
                text = { stringResource(it.nameRes) },
                selected = formData.ammunitionWeaponGroups,
            )
            DamageInput(formData, validate)
            WeaponQualitiesPicker(formData)
            WeaponFlawsPicker(formData)
        }
        TrappingTypeOption.ARMOUR -> {
            SelectBox(
                label = stringResource(R.string.label_armour_type),
                value = formData.armourType.value,
                onValueChange = { formData.armourType.value = it },
                items = remember { ArmourType.values() },
            )
            ArmourLocationsPickers(formData, validate)
            TextInput(
                label = stringResource(R.string.label_armour_points),
                value = formData.armourPoints,
                validate = validate,
            )
            WornCheckbox(formData)
        }
        TrappingTypeOption.CONTAINER -> {
            TextInput(
                label = stringResource(R.string.label_carries),
                value = formData.carries,
                validate = validate,
            )
        }
        TrappingTypeOption.MELEE_WEAPON -> {
            SelectBox(
                label = stringResource(R.string.label_weapon_group),
                value = formData.meleeWeaponGroup.value,
                onValueChange = { formData.meleeWeaponGroup.value = it },
                items = remember { MeleeWeaponGroup.values() },
            )
            SelectBox(
                label = stringResource(R.string.label_reach),
                value = formData.weaponReach.value,
                onValueChange = { formData.weaponReach.value = it },
                items = remember { Reach.values() },
            )
            DamageInput(formData, validate)
            WeaponQualitiesPicker(formData)
            WeaponFlawsPicker(formData)
        }
        TrappingTypeOption.RANGED_WEAPON -> {
            SelectBox(
                label = stringResource(R.string.label_weapon_group),
                value = formData.rangedWeaponGroup.value,
                onValueChange = { formData.rangedWeaponGroup.value = it },
                items = remember { RangedWeaponGroup.values() },
            )
            TextInput(
                label = stringResource(R.string.label_range),
                value = formData.weaponRange,
                helperText = stringResource(R.string.helper_weapon_range),
                validate = validate,
            )
            DamageInput(formData, validate)
            WeaponQualitiesPicker(formData)
            WeaponFlawsPicker(formData)
        }
        TrappingTypeOption.MISCELLANEOUS -> {}
    }
}

@Composable
private fun WornCheckbox(formData: TrappingTypeFormData) {
    CheckboxWithText(
        text = stringResource(R.string.label_worn),
        checked = formData.worn.value,
        onCheckedChange = { formData.worn.value = it }
    )
}

@Composable
private fun ArmourLocationsPickers(formData: TrappingTypeFormData, validate: Boolean) {
    val selectedParts = formData.armourLocations

    InputLabel(stringResource(R.string.label_armour_locations))

    CheckboxList(
        items = ArmourLocation.values(),
        text = { stringResource(it.nameRes) },
        selected = selectedParts,
    )

    if (validate && selectedParts.value.isEmpty()) {
        ErrorMessage(stringResource(R.string.error_armour_location_required))
    }
}

@Composable
private fun <T> CheckboxList(
    items: Array<T>,
    text: @Composable (T) -> String,
    selected: MutableState<Set<T>>,
) {
    Column {
        items.forEach { item ->
            CheckboxWithText(
                text = text(item),
                checked = item in selected.value,
                onCheckedChange = { checked ->
                    if (checked) {
                        selected.value += item
                    } else {
                        selected.value -= item
                    }
                }
            )
        }
    }
}

@Composable
private fun DamageInput(formData: TrappingTypeFormData, validate: Boolean) {
    TextInput(
        label = stringResource(R.string.label_damage),
        value = formData.damage,
        helperText = stringResource(R.string.helper_damage),
        validate = validate,
    )
}

@Composable
private fun WeaponQualitiesPicker(formData: TrappingTypeFormData) {
    val values = formData.weaponQualities

    Column {
        InputLabel(stringResource(R.string.label_weapon_qualities))
        WeaponQuality.values().forEach { quality ->
            CheckboxWithText(
                text = stringResource(quality.nameRes),
                checked = values.containsKey(quality),
                onCheckedChange = { checked ->
                    if (checked) {
                        values[quality] = 1
                    } else {
                        values.remove(quality)
                    }
                },
                badge = {
                    val rating = values[quality]

                    if (rating != null && quality.hasRating) {
                        NumberPicker(
                            value = rating,
                            onIncrement = { values[quality] = rating + 1 },
                            onDecrement = { values[quality] = (rating - 1).coerceAtLeast(1) },
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun WeaponFlawsPicker(formData: TrappingTypeFormData) {
    val values = formData.weaponFlaws

    Column {
        InputLabel(stringResource(R.string.label_weapon_flaws))
        WeaponFlaw.values().forEach { flaw ->
            CheckboxWithText(
                text = stringResource(flaw.nameRes),
                checked = values.containsKey(flaw),
                onCheckedChange = { checked ->
                    if (checked) {
                        values[flaw] = 1
                    } else {
                        values.remove(flaw)
                    }
                },
                badge = {
                    val rating = values[flaw]

                    if (rating != null && flaw.hasRating) {
                        NumberPicker(
                            value = rating,
                            onIncrement = { values[flaw] = rating + 1 },
                            onDecrement = { values[flaw] = (rating - 1).coerceAtLeast(1) },
                        )
                    }
                }
            )
        }
    }
}

private class InventoryItemFormData(
    val id: InventoryItemId,
    val name: InputValue,
    val encumbrance: InputValue,
    val quantity: InputValue,
    val description: InputValue,
    val type: TrappingTypeFormData,
) : HydratedFormData<InventoryItem> {
    override fun isValid() =
        listOf(name, encumbrance, quantity, description).all { it.isValid() } && type.isValid()

    override fun toValue() = InventoryItem(
        id = id,
        name = name.value,
        description = description.value,
        quantity = max(1, quantity.toInt()),
        encumbrance = Encumbrance(encumbrance.toDouble()),
        trappingType = type.toValue(),
    )

    companion object {
        @Composable
        fun fromItem(item: InventoryItem?) = InventoryItemFormData(
            id = remember(item) { item?.id ?: UUID.randomUUID() },
            name = inputValue(item?.name ?: "", Rules.NotBlank()),
            encumbrance = inputValue(item?.encumbrance?.toString() ?: "0"),
            quantity = inputValue(item?.quantity?.toString() ?: "1"),
            description = inputValue(item?.description ?: ""),
            type = TrappingTypeFormData.fromTrappingType(item?.trappingType)
        )
    }
}

private class TrappingTypeFormData(
    val type: MutableState<TrappingTypeOption>,
    val ammunitionRange: InputValue,
    val ammunitionWeaponGroups: MutableState<Set<RangedWeaponGroup>>,
    val armourPoints: InputValue,
    val armourLocations: MutableState<Set<ArmourLocation>>,
    val armourType: MutableState<ArmourType>,
    val carries: InputValue,
    val damage: InputValue,
    val meleeWeaponGroup: MutableState<MeleeWeaponGroup>,
    val rangedWeaponGroup: MutableState<RangedWeaponGroup>,
    val weaponEquipped: MutableState<WeaponEquip?>,
    val weaponFlaws: SnapshotStateMap<WeaponFlaw, Int>,
    val weaponRange: InputValue,
    val weaponReach: MutableState<Reach>,
    val weaponQualities: SnapshotStateMap<WeaponQuality, Int>,
    val worn: MutableState<Boolean>,
) : HydratedFormData<TrappingType?> {
    override fun isValid(): Boolean = when (type.value) {
        TrappingTypeOption.AMMUNITION -> {
            ammunitionRange.isValid() &&
                ammunitionWeaponGroups.value.isNotEmpty() &&
                damage.isValid()
        }
        TrappingTypeOption.ARMOUR -> armourLocations.value.isNotEmpty() && armourPoints.isValid()
        TrappingTypeOption.CONTAINER -> carries.isValid()
        TrappingTypeOption.MELEE_WEAPON -> damage.isValid()
        TrappingTypeOption.RANGED_WEAPON -> damage.isValid() && weaponRange.isValid()
        TrappingTypeOption.MISCELLANEOUS -> true
    }

    override fun toValue(): TrappingType? = when (type.value) {
        TrappingTypeOption.AMMUNITION -> TrappingType.Ammunition(
            weaponGroups = ammunitionWeaponGroups.value,
            range = AmmunitionRangeExpression(ammunitionRange.value),
            qualities = weaponQualities.toMap(),
            flaws = weaponFlaws.toMap(),
            damage = DamageExpression(damage.value),
        )
        TrappingTypeOption.ARMOUR -> TrappingType.Armour(
            locations = armourLocations.value,
            points = ArmourPoints(armourPoints.toInt()),
            type = armourType.value,
            worn = worn.value,
        )
        TrappingTypeOption.CONTAINER -> TrappingType.Container(
            carries = Encumbrance(carries.toDouble()),
            worn = worn.value,
        )
        TrappingTypeOption.MELEE_WEAPON -> TrappingType.MeleeWeapon(
            group = meleeWeaponGroup.value,
            reach = weaponReach.value,
            damage = DamageExpression(damage.value),
            qualities = weaponQualities.toMap(),
            flaws = weaponFlaws.toMap(),
            equipped = weaponEquipped.value,
        )
        TrappingTypeOption.RANGED_WEAPON -> TrappingType.RangedWeapon(
            group = rangedWeaponGroup.value,
            range = WeaponRangeExpression(weaponRange.value),
            damage = DamageExpression(damage.value),
            qualities = weaponQualities.toMap(),
            flaws = weaponFlaws.toMap(),
            equipped = weaponEquipped.value,
        )
        TrappingTypeOption.MISCELLANEOUS -> null
    }

    companion object {
        @Composable
        fun fromTrappingType(type: TrappingType?): TrappingTypeFormData {
            return when (type) {
                null -> fromDefaults(TrappingTypeOption.MISCELLANEOUS)
                is TrappingType.Ammunition -> fromDefaults(
                    type = TrappingTypeOption.AMMUNITION,
                    ammunitionRange = type.range,
                    ammunitionWeaponGroups = type.weaponGroups,
                    damage = type.damage,
                    weaponQualities = type.qualities,
                    weaponFlaws = type.flaws,
                )
                is TrappingType.Armour -> fromDefaults(
                    type = TrappingTypeOption.ARMOUR,
                    armourType = type.type,
                    armourLocations = type.locations,
                    armourPoints = type.points,
                    worn = type.worn,
                )
                is TrappingType.Container -> fromDefaults(
                    type = TrappingTypeOption.CONTAINER,
                    carries = type.carries,
                    worn = type.worn,
                )
                is TrappingType.MeleeWeapon -> fromDefaults(
                    type = TrappingTypeOption.MELEE_WEAPON,
                    damage = type.damage,
                    meleeWeaponGroup = type.group,
                    weaponReach = type.reach,
                    weaponQualities = type.qualities,
                    weaponFlaws = type.flaws,
                    weaponEquipped = type.equipped,
                )
                is TrappingType.RangedWeapon -> fromDefaults(
                    type = TrappingTypeOption.RANGED_WEAPON,
                    damage = type.damage,
                    rangedWeaponGroup = type.group,
                    weaponRange = type.range,
                    weaponQualities = type.qualities,
                    weaponFlaws = type.flaws,
                    weaponEquipped = type.equipped,
                )
            }
        }

        @Composable
        private fun fromDefaults(
            type: TrappingTypeOption,
            ammunitionRange: AmmunitionRangeExpression? = null,
            ammunitionWeaponGroups: Set<RangedWeaponGroup> = emptySet(),
            armourLocations: Set<ArmourLocation> = emptySet(),
            armourPoints: ArmourPoints? = null,
            armourType: ArmourType = ArmourType.SOFT_LEATHER,
            carries: Encumbrance? = null,
            damage: DamageExpression? = null,
            meleeWeaponGroup: MeleeWeaponGroup = MeleeWeaponGroup.BASIC,
            rangedWeaponGroup: RangedWeaponGroup = RangedWeaponGroup.BLACKPOWDER,
            weaponQualities: Map<WeaponQuality, Int> = emptyMap(),
            weaponEquipped: WeaponEquip? = null,
            weaponFlaws: Map<WeaponFlaw, Int> = emptyMap(),
            weaponRange: WeaponRangeExpression? = null,
            weaponReach: Reach = Reach.AVERAGE,
            worn: Boolean = false,
        ) = TrappingTypeFormData(
            type = rememberSaveable { mutableStateOf(type) },
            armourLocations = rememberSaveable { mutableStateOf(armourLocations) },
            armourPoints = inputValue(
                armourPoints?.value?.toString() ?: "",
                Rules.NonNegativeInteger(),
            ),
            ammunitionRange = inputValue(
                ammunitionRange?.value ?: "",
                Rules.NotBlank(),
                Rule(R.string.error_invalid_expression, AmmunitionRangeExpression::isValid)
            ),
            ammunitionWeaponGroups = rememberSaveable { mutableStateOf(ammunitionWeaponGroups) },
            armourType = rememberSaveable { mutableStateOf(armourType) },
            carries = inputValue(carries?.toString() ?: "", Rules.NonNegativeInteger()),
            damage = inputValue(
                damage?.value ?: "",
                Rules.NotBlank(),
                Rule(R.string.error_invalid_expression, DamageExpression::isValid),
            ),
            meleeWeaponGroup = rememberSaveable { mutableStateOf(meleeWeaponGroup) },
            rangedWeaponGroup = rememberSaveable { mutableStateOf(rangedWeaponGroup) },
            weaponEquipped = rememberSaveable { mutableStateOf(weaponEquipped) },
            weaponFlaws = remember { stateMapFrom(weaponFlaws) },
            weaponQualities = remember { stateMapFrom(weaponQualities) },
            weaponRange = inputValue(
                weaponRange?.value ?: "",
                Rules.NotBlank(),
                Rule(R.string.error_invalid_expression, WeaponRangeExpression::isValid),
            ),
            weaponReach = rememberSaveable { mutableStateOf(weaponReach) },
            worn = rememberSaveable { mutableStateOf(worn) },
        )

        private fun <K, V> stateMapFrom(map: Map<K, V>) =
            SnapshotStateMap<K, V>().apply { putAll(map) }
    }
}

private enum class TrappingTypeOption(@StringRes override val nameRes: Int) : NamedEnum {
    AMMUNITION(R.string.trapping_type_ammunition),
    ARMOUR(R.string.trapping_type_armour),
    CONTAINER(R.string.trapping_type_container),
    MELEE_WEAPON(R.string.trapping_type_melee_weapon),
    MISCELLANEOUS(R.string.trapping_type_miscellaneous),
    RANGED_WEAPON(R.string.trapping_type_ranged_weapon),
}
