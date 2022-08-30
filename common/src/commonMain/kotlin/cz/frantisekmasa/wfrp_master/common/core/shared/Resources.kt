package cz.frantisekmasa.wfrp_master.common.core.shared

object Resources {
    enum class Drawable(val path: String) {

        // Characteristics
        Agility("drawable/ic_agility.xml"),
        BallisticSkill("drawable/ic_ballistic_skill.xml"),
        Dexterity("drawable/ic_dexterity.xml"),
        Fellowship("drawable/ic_fellowship.xml"),
        Intelligence("drawable/ic_brain.xml"),
        Initiative("drawable/ic_initiative.xml"),
        Strength("drawable/ic_strength.xml"),
        Toughness("drawable/ic_toughness.xml"),
        WeaponSkill("drawable/ic_weapon_skill.xml"),
        WillPower("drawable/ic_will_power.xml"),

        Dead("drawable/ic_dead.xml"),
        DefaultAvatarIcon("drawable/ic_face.xml"),
        DiceRoll("drawable/ic_dice_roll.xml"),
        Encounter("drawable/ic_encounter.xml"),
        MemorizeSpell("drawable/ic_brain.xml"),
        None("drawable/ic_none.xml"),
        Npc("drawable/ic_npc.xml"),
        PartyNotFound("drawable/ic_rally_the_troops.xml"),
        SplashScreenIcon("drawable/splash_screen_icon.xml"),

        // Items
        Blessing("drawable/ic_pray.xml"),
        Miracle("drawable/ic_pray.xml"),
        Skill("drawable/ic_skills.xml"),
        Spell("drawable/ic_spells.xml"),
        Talent("drawable/ic_skills.xml"),
        Trait("drawable/ic_traits.xml"),

        // Conditions
        ConditionAblaze("drawable/ic_condition_ablaze.xml"),
        ConditionBlinded("drawable/ic_condition_blinded.xml"),
        ConditionBleeding("drawable/ic_condition_bleeding.xml"),
        ConditionBroken("drawable/ic_condition_broken.xml"),
        ConditionDeafened("drawable/ic_condition_deafened.xml"),
        ConditionEntangled("drawable/ic_condition_entangled.xml"),
        ConditionFatigued("drawable/ic_condition_fatigued.xml"),
        ConditionPoisoned("drawable/ic_condition_poisoned.xml"),
        ConditionProne("drawable/ic_condition_prone.xml"),
        ConditionStunned("drawable/ic_condition_stunned.xml"),
        ConditionSurprised("drawable/ic_condition_surprised.xml"),
        ConditionUnconscious("drawable/ic_condition_unconscious.xml"),

        // Trappings
        TrappingAmmunition("drawable/ic_ammunition.xml"),
        TrappingCoins("drawable/ic_coins.xml"),
        TrappingContainer("drawable/ic_container.xml"),
        TrappingMiscellaneous("drawable/ic_miscellaneous.xml"),
        TrappingEncumbrance("drawable/ic_encumbrance.xml"),

        // Armor
        ArmorArmLeft("drawable/ic_armor_arm_left.xml"),
        ArmorArmRight("drawable/ic_armor_arm_right.xml"),
        ArmorChest("drawable/ic_armor_chest.xml"),
        ArmorHead("drawable/ic_armor_head.xml"),
        ArmorLegLeft("drawable/ic_armor_leg_left.xml"),
        ArmorLegRight("drawable/ic_armor_leg_right.xml"),
        ArmorShield("drawable/ic_armor_shield.xml"),

        // Raster images
        GoogleLogo("drawable/google_logo.png"),
    }

    enum class Sound(val path: String) {
        DiceRoll("raw/roll_sound.wav")
    }
}
