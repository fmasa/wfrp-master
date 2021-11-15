package cz.frantisekmasa.wfrp_master.common.localization

data class Strings(
    val socialStatusBrass: String = "Brass",
    val socialStatusGold: String = "Gold",
    val socialStatusSilver: String = "Silver",

    val labelFatePoints: String = "Fate",
    val labelFortunePoints: String = "Fortune",
    val labelResiliencePoints: String = "Resilience",
    val labelResolvePoints: String = "Resolve",

    val characteristicAgility: String = "Agility",
    val characteristicBallisticSkill: String = "Ballistic Skill",
    val characteristicDexterity: String = "Dexterity",
    val characteristicFellowship: String = "Fellowship",
    val characteristicInitiative: String = "Initiative",
    val characteristicIntelligence: String = "Intelligence",
    val characteristicStrength: String = "Strength",
    val characteristicToughness: String = "Toughness",
    val characteristicWeaponSkill: String = "Weapon Skill",
    val characteristicWillPower: String = "Will Power",

    val testResultAstoundingSuccess : String = "Astounding Success",
    val testResultImpressiveSuccess : String = "Impressive Success",
    val testResultSuccess: String = "Success",
    val testResultMarginalSuccess : String = "Marginal Success",
    val testResultMarginalFailure : String = "Marginal Failure",
    val testResultFailure: String = "Failure",
    val testResultImpressiveFailure : String = "Impressive Failure",
    val testResultAstoundingFailure : String = "Astounding Failure",


    val races: RaceStrings = RaceStrings(),
    val trappings: TrappingStrings = TrappingStrings(),
    val weapons: WeaponStrings = WeaponStrings(),

    val commonUi: CommonUiStrings = CommonUiStrings(),
    val validation: ValidationStrings = ValidationStrings(),
    /*
     * Armour
     */

    val armourShield: String = "Shield",
    val armourLeftLeg: String = "Left Leg",
    val armourRightLeg: String = "Right Leg",
    val armourLeftArm: String = "Left Arm",
    val armourRightArm: String = "Right Arm",
    val armourBody: String = "Body",
    val armourHead: String = "Head",

    val armourTypeBoiledLeather: String = "Boiled Leather",
    val armourTypeMail: String = "Mail",
    val armourTypeOther: String = "Other",
    val armourTypePlate: String = "Plate",
    val armourTypeSoftLeather: String = "Soft Leather",
)

data class CommonUiStrings(
    val labelCloseDialog: String = "Close current dialog",
    val labelExpandSelectBox: String = "Expand select box",
    val labelOpenDrawer: String = "Open navigation drawer",
    val labelOpenContextMenu: String = "Open context menu",
    val labelPreviousScreen: String = "Return to previous screen",
    val buttonSave: String = "Save",
    val decrement: String = "Decrement value",
    val increment: String = "Increment value",
)

data class ValidationStrings(
    val integer: String = "Must be number without decimals",
    val notBlank: String = "Cannot be empty",
    val nonNegative: String = "Must be greater or equal to 0",
    val positiveInteger: String = "Must be positive number without decimals",
    val required: String = "Required",

    )

data class WeaponStrings(
    val flaws: WeaponFlawStrings = WeaponFlawStrings(),
    val meleeGroups: MeleeWeaponGroupStrings = MeleeWeaponGroupStrings(),
    val qualities: WeaponQualityStrings = WeaponQualityStrings(),
    val rangedGroups: RangedWeaponGroupStrings = RangedWeaponGroupStrings(),
    val reach: WeaponReachStrings = WeaponReachStrings(),
)

data class WeaponFlawStrings(
    val dangerous: String = "Dangerous",
    val imprecise: String = "Imprecise",
    val reload: String = "Reload",
    val slow: String = "Slow",
    val tiring: String = "Tiring",
    val undamaging: String = "Undamaging",
)

data class MeleeWeaponGroupStrings(
    val basic: String = "Basic",
    val brawling: String = "Brawling",
    val cavalry: String = "Cavalry",
    val fencing: String = "Fencing",
    val flail: String = "Flail",
    val parry: String = "Parry",
    val polearm: String = "Polearm",
    val twoHanded: String = "Two-Handed",
)

data class WeaponQualityStrings(
    val accurate: String = "Accurate",
    val blackpowder: String = "Blackpowder",
    val blast: String = "Blast",
    val damaging: String = "Damaging",
    val defensive: String = "Defensive",
    val distract: String = "Distract",
    val entangle: String = "Entangle",
    val fast: String = "Fast",
    val hack: String = "Hack",
    val impact: String = "Impact",
    val impale: String = "Impale",
    val penetrating: String = "Penetrating",
    val pistol: String = "Pistol",
    val precise: String = "Precise",
    val pummel: String = "Pummel",
    val repeater: String = "Repeater",
    val shield: String = "Shield",
    val trapBlade: String = "Trap Blade",
    val unbreakable: String = "Unbreakable",
    val wrap: String = "Wrap",
)

data class RangedWeaponGroupStrings(
    val blackpowder: String = "Blackpowder",
    val bow: String = "Bow",
    val crossbow: String = "Crossbow",
    val entangling: String = "Entangling",
    val engineering: String = "Engineering",
    val explosives: String = "Explosives",
    val sling: String = "Sling",
    val throwing: String = "Throwing",
)

data class WeaponReachStrings(
    val personal: String = "Personal",
    val veryShort: String = "Very Short",
    val short: String = "Short",
    val average: String = "Average",
    val long: String = "Long",
    val veryLong: String = "Very Long",
    val massive: String = "Massive",
)


data class TrappingStrings(
    val types: TrappingTypeStrings = TrappingTypeStrings(),
)

data class TrappingTypeStrings(
    val ammunition: String = "Ammunition",
    val armour: String = "Armour",
    val container: String = "Container",
    val meleeWeapon: String = "Melee Weapon",
    val miscellaneous: String = "Miscellaneous",
    val rangedWeapon: String = "Ranged Weapon",
)

data class RaceStrings(
    val dwarf: String = "Dwarf",
    val highElf : String = "High Elf",
    val woodElf : String = "Wood Elf",
    val halfling: String = "Halfling",
    val human: String = "Human",
    val gnome: String = "Gnome",
)