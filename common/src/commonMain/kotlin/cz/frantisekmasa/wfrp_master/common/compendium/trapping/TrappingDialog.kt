package cz.frantisekmasa.wfrp_master.common.compendium.trapping

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.trappings.TrappingTypeOption
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trapping
import cz.frantisekmasa.wfrp_master.common.compendium.domain.TrappingType
import cz.frantisekmasa.wfrp_master.common.core.domain.HitLocation
import cz.frantisekmasa.wfrp_master.common.core.domain.Money
import cz.frantisekmasa.wfrp_master.common.core.domain.localizedName
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.AmmunitionRangeExpression
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.ArmourFlaw
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.ArmourPoints
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.ArmourQuality
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.ArmourType
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Availability
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.DamageExpression
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Encumbrance
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItem
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.MeleeWeaponGroup
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.RangedWeaponGroup
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Reach
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.TrappingFeature
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.WeaponFlaw
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.WeaponQuality
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.WeaponRangeExpression
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.CheckboxList
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.CheckboxWithText
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.ErrorMessage
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Filter
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.FormDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.HydratedFormData
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputLabel
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.NumberPicker
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.SelectBox
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.inputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.rule
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun TrappingDialog(
    onSaveRequest: suspend (Trapping) -> Unit,
    existingTrapping: Trapping?,
    onDismissRequest: () -> Unit,
) {
    FullScreenDialog(onDismissRequest = onDismissRequest) {
        val formData = TrappingFormData.fromItem(existingTrapping)

        FormDialog(
            title = stringResource(
                if (existingTrapping != null)
                    Str.trappings_title_edit
                else Str.trappings_title_add
            ),
            onDismissRequest = onDismissRequest,
            formData = formData,
            onSave = onSaveRequest,
        ) { validate ->
            TextInput(
                label = stringResource(Str.trappings_label_name),
                value = formData.name,
                validate = validate,
                maxLength = InventoryItem.NAME_MAX_LENGTH,
            )

            TextInput(
                label = stringResource(Str.trappings_label_encumbrance_per_unit),
                value = formData.encumbrance,
                maxLength = 8,
                validate = validate,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                filters = listOf(Filter.DigitsAndDotSymbolsOnly),
            )

            TextInput(
                label = stringResource(Str.trappings_label_description),
                value = formData.description,
                validate = validate,
                maxLength = InventoryItem.DESCRIPTION_MAX_LENGTH,
                multiLine = true,
                helperText = stringResource(Str.common_ui_markdown_supported_note),
            )

            Divider()

            SelectBox(
                label = stringResource(Str.trappings_label_availability),
                value = formData.availability.value,
                onValueChange = { formData.availability.value = it },
                items = remember { Availability.values() },
            )

            Text(stringResource(Str.trappings_label_price))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.small),
            ) {
                CoinInput(
                    formData.price.crowns,
                    stringResource(Str.trappings_money_crowns),
                    validate,
                )
                CoinInput(
                    formData.price.shillings,
                    stringResource(Str.trappings_money_shillings),
                    validate,
                )
                CoinInput(
                    formData.price.pennies,
                    stringResource(Str.trappings_money_pennies),
                    validate,
                )
            }

            TextInput(
                label = stringResource(Str.trappings_label_pack_size),
                value = formData.packSize,
                validate = validate
            )

            Divider()

            TrappingTypeForm(
                formData = formData.type,
                validate = validate,
            )
        }
    }
}

@Composable
private fun RowScope.CoinInput(value: InputValue, label: String, validate: Boolean) {
    Column(Modifier.weight(1f)) {
        TextInput(
            value = value,
            label = label,
            validate = validate,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            maxLength = 4,
            showCharacterCount = false,
            placeholder = "0",
        )
    }
}

@Composable
private fun TrappingTypeForm(formData: TrappingTypeFormData, validate: Boolean) {
    SelectBox(
        label = stringResource(Str.trappings_label_type),
        value = formData.type.value,
        onValueChange = { formData.type.value = it },
        items = remember { TrappingTypeOption.values() },
    )

    @Suppress("UNUSED_VARIABLE")
    val exhaustive = when (formData.type.value) {
        TrappingTypeOption.AMMUNITION -> {
            TextInput(
                label = stringResource(Str.weapons_label_range),
                value = formData.ammunitionRange,
                validate = validate,
                helperText = stringResource(
                    Str.common_ui_expression_helper,
                    remember {
                        AmmunitionRangeExpression.Constant.values()
                            .joinToString(", ") { it.value }
                    }
                ),
            )
            CheckboxList(
                items = RangedWeaponGroup.values(),
                text = { it.localizedName },
                selected = formData.ammunitionWeaponGroups,
            )
            DamageInput(formData, validate)
            WeaponQualitiesPicker(formData)
            WeaponFlawsPicker(formData)
        }

        TrappingTypeOption.ARMOUR -> {
            SelectBox(
                label = stringResource(Str.armour_label_type),
                value = formData.armourType.value,
                onValueChange = { formData.armourType.value = it },
                items = remember { ArmourType.values() },
            )
            ArmourLocationsPickers(formData, validate)
            TextInput(
                label = stringResource(Str.armour_label_armour_points),
                value = formData.armourPoints,
                validate = validate,
            )
            TrappingFeaturePicker(
                stringResource(Str.armour_label_qualities),
                ArmourQuality.values(),
                formData.armourQualities,
            )
            TrappingFeaturePicker(
                stringResource(Str.armour_label_flaws),
                ArmourFlaw.values(),
                formData.armourFlaws,
            )
        }

        TrappingTypeOption.CLOTHING_OR_ACCESSORY -> {
        }

        TrappingTypeOption.CONTAINER -> {
            TextInput(
                label = stringResource(Str.trappings_label_carries),
                value = formData.carries,
                validate = validate,
            )
        }

        TrappingTypeOption.MELEE_WEAPON -> {
            SelectBox(
                label = stringResource(Str.weapons_label_group),
                value = formData.meleeWeaponGroup.value,
                onValueChange = { formData.meleeWeaponGroup.value = it },
                items = remember { MeleeWeaponGroup.values() },
            )
            SelectBox(
                label = stringResource(Str.weapons_label_reach),
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
                label = stringResource(Str.weapons_label_group),
                value = formData.rangedWeaponGroup.value,
                onValueChange = { formData.rangedWeaponGroup.value = it },
                items = remember { RangedWeaponGroup.values() },
            )
            TextInput(
                label = stringResource(Str.weapons_label_range),
                value = formData.weaponRange,
                helperText = stringResource(
                    Str.common_ui_expression_helper,
                    remember {
                        WeaponRangeExpression.Constant.values()
                            .joinToString(", ") { it.value }
                    },
                ),
                validate = validate,
            )
            DamageInput(formData, validate)
            WeaponQualitiesPicker(formData)
            WeaponFlawsPicker(formData)
        }

        TrappingTypeOption.BOOK_OR_DOCUMENT,
        TrappingTypeOption.DRUG_OR_POISON,
        TrappingTypeOption.MISCELLANEOUS,
        TrappingTypeOption.FOOD_OR_DRINK,
        TrappingTypeOption.HERB_OR_DRAUGHT,
        TrappingTypeOption.SPELL_INGREDIENT,
        TrappingTypeOption.TOOL_OR_KIT,
        TrappingTypeOption.TRADE_TOOLS -> {
        }
    }
}

@Composable
private fun ArmourLocationsPickers(formData: TrappingTypeFormData, validate: Boolean) {
    val selectedParts = formData.armourLocations

    InputLabel(stringResource(Str.armour_label_locations))

    CheckboxList(
        items = HitLocation.values(),
        text = { it.localizedName },
        selected = selectedParts,
    )

    if (validate && selectedParts.value.isEmpty()) {
        ErrorMessage(stringResource(Str.armour_messages_at_least_one_location_required))
    }
}

@Composable
private fun DamageInput(formData: TrappingTypeFormData, validate: Boolean) {
    TextInput(
        label = stringResource(Str.weapons_label_damage),
        value = formData.damage,
        helperText = stringResource(
            Str.common_ui_expression_helper,
            remember { DamageExpression.Constant.values().joinToString(", ") { it.value } },
        ),
        validate = validate,
    )
}

@Composable
private fun WeaponQualitiesPicker(formData: TrappingTypeFormData) {
    TrappingFeaturePicker(
        stringResource(Str.weapons_label_qualities),
        WeaponQuality.values(),
        formData.weaponQualities
    )
}

@Composable
private fun WeaponFlawsPicker(formData: TrappingTypeFormData) {
    TrappingFeaturePicker(
        stringResource(Str.weapons_label_flaws),
        WeaponFlaw.values(),
        formData.weaponFlaws
    )
}

@Composable
private fun <T : TrappingFeature> TrappingFeaturePicker(
    label: String,
    options: Array<T>,
    values: SnapshotStateMap<T, Int>
) {
    Column {
        InputLabel(label)
        options.forEach { quality ->
            CheckboxWithText(
                text = quality.localizedName,
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

@Stable
private class PriceFormData(
    val crowns: InputValue,
    val shillings: InputValue,
    val pennies: InputValue,
) : HydratedFormData<Money> {
    override fun isValid(): Boolean = listOf(crowns, shillings, pennies).all { it.isValid() }

    override fun toValue(): Money {
        return Money.sum(
            Money.crowns(crowns.toIntOrNull() ?: 0),
            Money.shillings(shillings.toIntOrNull() ?: 0),
            Money.pennies(pennies.toIntOrNull() ?: 0),
        )
    }

    companion object {
        @Composable
        fun fromMoney(money: Money?): PriceFormData {
            return PriceFormData(
                crowns = inputValue(
                    money?.getCrowns()?.toString() ?: "",
                    Rules.NonNegativeInteger(),
                ),
                shillings = inputValue(
                    money?.getShillings()?.toString() ?: "",
                    Rules.NonNegativeInteger(),
                ),
                pennies = inputValue(
                    money?.getPennies()?.toString() ?: "",
                    Rules.NonNegativeInteger(),
                ),
            )
        }
    }
}

@Stable
private class TrappingFormData(
    val id: Uuid,
    val name: InputValue,
    val encumbrance: InputValue,
    val availability: MutableState<Availability>,
    val price: PriceFormData,
    val packSize: InputValue,
    val description: InputValue,
    val type: TrappingTypeFormData,
    val isVisibleToPlayers: Boolean,
) : HydratedFormData<Trapping> {
    override fun isValid() =
        listOf(name, encumbrance, description).all { it.isValid() } && type.isValid()

    override fun toValue(): Trapping {
        return Trapping(
            id = id,
            name = name.value,
            description = description.value,
            encumbrance = Encumbrance(encumbrance.toDouble()),
            trappingType = type.toValue(),
            availability = availability.value,
            price = price.toValue(),
            packSize = packSize.toInt(),
            isVisibleToPlayers = isVisibleToPlayers,
        )
    }

    companion object {
        @Composable
        fun fromItem(item: Trapping?) = TrappingFormData(
            id = remember(item) { item?.id ?: uuid4() },
            name = inputValue(item?.name ?: "", Rules.NotBlank()),
            encumbrance = inputValue(item?.encumbrance?.toString() ?: "0"),
            description = inputValue(item?.description ?: ""),
            type = TrappingTypeFormData.fromTrappingType(item?.trappingType),
            isVisibleToPlayers = item?.isVisibleToPlayers ?: false,
            availability = rememberSaveable(item) {
                mutableStateOf(item?.availability ?: Availability.EXOTIC)
            },
            price = PriceFormData.fromMoney(item?.price),
            packSize = inputValue((item?.packSize ?: 1).toString(), Rules.PositiveInteger()),
        )
    }
}

private class TrappingTypeFormData(
    val type: MutableState<TrappingTypeOption>,
    val ammunitionRange: InputValue,
    val ammunitionWeaponGroups: MutableState<Set<RangedWeaponGroup>>,
    val armourPoints: InputValue,
    val armourLocations: MutableState<Set<HitLocation>>,
    val armourType: MutableState<ArmourType>,
    val armourQualities: SnapshotStateMap<ArmourQuality, Int>,
    val armourFlaws: SnapshotStateMap<ArmourFlaw, Int>,
    val carries: InputValue,
    val damage: InputValue,
    val meleeWeaponGroup: MutableState<MeleeWeaponGroup>,
    val rangedWeaponGroup: MutableState<RangedWeaponGroup>,
    val weaponFlaws: SnapshotStateMap<WeaponFlaw, Int>,
    val weaponRange: InputValue,
    val weaponReach: MutableState<Reach>,
    val weaponQualities: SnapshotStateMap<WeaponQuality, Int>,
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
        TrappingTypeOption.BOOK_OR_DOCUMENT,
        TrappingTypeOption.CLOTHING_OR_ACCESSORY,
        TrappingTypeOption.DRUG_OR_POISON,
        TrappingTypeOption.FOOD_OR_DRINK,
        TrappingTypeOption.HERB_OR_DRAUGHT,
        TrappingTypeOption.MISCELLANEOUS,
        TrappingTypeOption.SPELL_INGREDIENT,
        TrappingTypeOption.TOOL_OR_KIT,
        TrappingTypeOption.TRADE_TOOLS -> true
    }

    override fun toValue(): TrappingType? = when (type.value) {
        TrappingTypeOption.AMMUNITION -> TrappingType.Ammunition(
            weaponGroups = ammunitionWeaponGroups.value,
            range = AmmunitionRangeExpression(ammunitionRange.value),
            qualities = weaponQualities.toMap(),
            flaws = weaponFlaws.toMap(),
            damage = DamageExpression(damage.value.trim()),
        )

        TrappingTypeOption.ARMOUR -> TrappingType.Armour(
            locations = armourLocations.value,
            points = ArmourPoints(armourPoints.toInt()),
            type = armourType.value,
            qualities = armourQualities.toMap(),
            flaws = armourFlaws.toMap(),
        )

        TrappingTypeOption.BOOK_OR_DOCUMENT -> TrappingType.BookOrDocument
        TrappingTypeOption.CLOTHING_OR_ACCESSORY -> TrappingType.ClothingOrAccessory

        TrappingTypeOption.CONTAINER -> TrappingType.Container(
            carries = Encumbrance(carries.toDouble()),
        )

        TrappingTypeOption.DRUG_OR_POISON -> TrappingType.DrugOrPoison
        TrappingTypeOption.FOOD_OR_DRINK -> TrappingType.FoodOrDrink
        TrappingTypeOption.HERB_OR_DRAUGHT -> TrappingType.HerbOrDraught
        TrappingTypeOption.MELEE_WEAPON -> TrappingType.MeleeWeapon(
            group = meleeWeaponGroup.value,
            reach = weaponReach.value,
            damage = DamageExpression(damage.value.trim()),
            qualities = weaponQualities.toMap(),
            flaws = weaponFlaws.toMap(),
        )

        TrappingTypeOption.RANGED_WEAPON -> TrappingType.RangedWeapon(
            group = rangedWeaponGroup.value,
            range = WeaponRangeExpression(weaponRange.value),
            damage = DamageExpression(damage.value.trim()),
            qualities = weaponQualities.toMap(),
            flaws = weaponFlaws.toMap(),
        )

        TrappingTypeOption.SPELL_INGREDIENT -> TrappingType.SpellIngredient
        TrappingTypeOption.MISCELLANEOUS -> null
        TrappingTypeOption.TOOL_OR_KIT -> TrappingType.ToolOrKit
        TrappingTypeOption.TRADE_TOOLS -> TrappingType.TradeTools
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
                    armourQualities = type.qualities,
                    armourFlaws = type.flaws,
                )

                is TrappingType.BookOrDocument -> fromDefaults(TrappingTypeOption.BOOK_OR_DOCUMENT)
                is TrappingType.ClothingOrAccessory -> fromDefaults(
                    type = TrappingTypeOption.CLOTHING_OR_ACCESSORY,
                )

                is TrappingType.Container -> fromDefaults(
                    type = TrappingTypeOption.CONTAINER,
                    carries = type.carries,
                )

                is TrappingType.DrugOrPoison -> fromDefaults(TrappingTypeOption.DRUG_OR_POISON)
                is TrappingType.HerbOrDraught -> fromDefaults(TrappingTypeOption.HERB_OR_DRAUGHT)
                is TrappingType.MeleeWeapon -> fromDefaults(
                    type = TrappingTypeOption.MELEE_WEAPON,
                    damage = type.damage,
                    meleeWeaponGroup = type.group,
                    weaponReach = type.reach,
                    weaponQualities = type.qualities,
                    weaponFlaws = type.flaws,
                )

                is TrappingType.RangedWeapon -> fromDefaults(
                    type = TrappingTypeOption.RANGED_WEAPON,
                    damage = type.damage,
                    rangedWeaponGroup = type.group,
                    weaponRange = type.range,
                    weaponQualities = type.qualities,
                    weaponFlaws = type.flaws,
                )

                is TrappingType.SpellIngredient -> fromDefaults(TrappingTypeOption.SPELL_INGREDIENT)
                TrappingType.FoodOrDrink -> fromDefaults(TrappingTypeOption.FOOD_OR_DRINK)
                TrappingType.ToolOrKit -> fromDefaults(TrappingTypeOption.TOOL_OR_KIT)
                TrappingType.TradeTools -> fromDefaults(TrappingTypeOption.TRADE_TOOLS)
            }
        }

        @Composable
        private fun fromDefaults(
            type: TrappingTypeOption,
            ammunitionRange: AmmunitionRangeExpression? = null,
            ammunitionWeaponGroups: Set<RangedWeaponGroup> = emptySet(),
            armourLocations: Set<HitLocation> = emptySet(),
            armourPoints: ArmourPoints? = null,
            armourType: ArmourType = ArmourType.SOFT_LEATHER,
            armourQualities: Map<ArmourQuality, Int> = emptyMap(),
            armourFlaws: Map<ArmourFlaw, Int> = emptyMap(),
            carries: Encumbrance? = null,
            damage: DamageExpression? = null,
            meleeWeaponGroup: MeleeWeaponGroup = MeleeWeaponGroup.BASIC,
            rangedWeaponGroup: RangedWeaponGroup = RangedWeaponGroup.BLACKPOWDER,
            weaponQualities: Map<WeaponQuality, Int> = emptyMap(),
            weaponFlaws: Map<WeaponFlaw, Int> = emptyMap(),
            weaponRange: WeaponRangeExpression? = null,
            weaponReach: Reach = Reach.AVERAGE,
        ): TrappingTypeFormData {
            val invalidExpressionMessage = stringResource(Str.validation_invalid_expression)

            return TrappingTypeFormData(
                type = rememberSaveable { mutableStateOf(type) },
                armourLocations = rememberSaveable { mutableStateOf(armourLocations) },
                armourPoints = inputValue(
                    armourPoints?.value?.toString() ?: "",
                    Rules.NonNegativeInteger(),
                ),
                armourQualities = remember { stateMapFrom(armourQualities) },
                armourFlaws = remember { stateMapFrom(armourFlaws) },
                ammunitionRange = inputValue(
                    ammunitionRange?.value ?: "",
                    Rules.NotBlank(),
                    rule(invalidExpressionMessage, AmmunitionRangeExpression::isValid)
                ),
                ammunitionWeaponGroups = rememberSaveable { mutableStateOf(ammunitionWeaponGroups) },
                armourType = rememberSaveable { mutableStateOf(armourType) },
                carries = inputValue(carries?.toString() ?: "", Rules.NonNegativeInteger()),
                damage = inputValue(
                    damage?.value ?: "",
                    Rules.NotBlank(),
                    rule(invalidExpressionMessage, DamageExpression::isValid),
                ),
                meleeWeaponGroup = rememberSaveable { mutableStateOf(meleeWeaponGroup) },
                rangedWeaponGroup = rememberSaveable { mutableStateOf(rangedWeaponGroup) },
                weaponFlaws = remember { stateMapFrom(weaponFlaws) },
                weaponQualities = remember { stateMapFrom(weaponQualities) },
                weaponRange = inputValue(
                    weaponRange?.value ?: "",
                    Rules.NotBlank(),
                    rule(invalidExpressionMessage, WeaponRangeExpression::isValid),
                ),
                weaponReach = rememberSaveable { mutableStateOf(weaponReach) },
            )
        }

        private fun <K, V> stateMapFrom(map: Map<K, V>) =
            SnapshotStateMap<K, V>().apply { putAll(map) }
    }
}
