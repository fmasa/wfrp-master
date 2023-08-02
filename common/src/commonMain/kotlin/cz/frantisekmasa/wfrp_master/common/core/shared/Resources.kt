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
        Character("drawable/ic_character.xml"),
        DiceRoll("drawable/ic_dice_roll.xml"),
        Encounter("drawable/ic_encounter.xml"),
        MemorizeSpell("drawable/ic_brain.xml"),
        None("drawable/ic_none.xml"),
        Npc("drawable/ic_npc.xml"),
        PartyNotFound("drawable/ic_rally_the_troops.xml"),
        SplashScreenIcon("drawable/splash_screen_icon.xml"),
        Compendium("drawable/ic_compendium.xml"),

        // Items
        Blessing("drawable/ic_pray.xml"),
        Miracle("drawable/ic_pray.xml"),
        Skill("drawable/ic_skills.xml"),
        Spell("drawable/ic_spells.xml"),
        Talent("drawable/ic_skills.xml"),
        Trait("drawable/ic_traits.xml"),
        Career("drawable/ic_skills.xml"), // TODO: Change the icon

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
        TrappingArmour("drawable/ic_armour.xml"),
        TrappingAmmunition("drawable/ic_ammunition.xml"),
        TrappingBookOrDocument("drawable/ic_book_or_document.xml"),
        TrappingClothingOrAccessory("drawable/ic_clothing_or_accessory.xml"),
        TrappingCoins("drawable/ic_coins.xml"),
        TrappingDrugOrPoison("drawable/ic_drug_or_poison.xml"),
        TrappingContainer("drawable/ic_container.xml"),
        TrappingFoodOrDrink("drawable/ic_food_or_drink.xml"),
        TrappingHerbOrDraught("drawable/ic_herb_or_draught.xml"),
        TrappingMiscellaneous("drawable/ic_miscellaneous.xml"),
        TrappingEncumbrance("drawable/ic_encumbrance.xml"),
        TrappingTool("drawable/ic_tool.xml"),

        LoreBeasts("drawable/ic_lore_beasts.xml"),
        LoreDeath("drawable/ic_lore_death.xml"),
        LoreFire("drawable/ic_lore_fire.xml"),
        LoreHeavens("drawable/ic_lore_heavens.xml"),
        LoreLife("drawable/ic_lore_life.xml"),
        LoreLight("drawable/ic_lore_light.xml"),
        LoreMetal("drawable/ic_lore_metal.xml"),
        LoreHedgecraft("drawable/ic_lore_hedgecraft.xml"),
        LoreWitchcraft("drawable/ic_lore_witchcraft.xml"),
        LoreDaemonology("drawable/ic_lore_daemonology.xml"),
        LoreNecromancy("drawable/ic_lore_necromancy.xml"),
        LorePettySpells("drawable/ic_lore_petty_spells.xml"),
        LoreShadows("drawable/ic_lore_shadows.xml"),
        LoreSlaanesh("drawable/ic_lore_slaanesh.xml"),
        LoreNurgle("drawable/ic_lore_nurgle.xml"),
        LoreTzeentch("drawable/ic_lore_tzeentch.xml"),

        // Raster images
        GoogleLogo("drawable/google_logo.png"),
    }

    enum class Sound(val path: String) {
        DiceRoll("raw/roll_sound.wav")
    }
}
