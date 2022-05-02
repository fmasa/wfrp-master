package cz.frantisekmasa.wfrp_master.common.localization

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

@Immutable
data class Strings(
    val socialStatusBrass: String = "Brass",
    val socialStatusGold: String = "Gold",
    val socialStatusSilver: String = "Silver",

    val about: AboutStrings = AboutStrings(),
    val ambition: AmbitionStrings = AmbitionStrings(),
    val armour: ArmourStrings = ArmourStrings(),
    val authentication: AuthenticationString = AuthenticationString(),
    val blessings: BlessingStrings = BlessingStrings(),
    val calendar: CalendarStrings = CalendarStrings(),
    val combat: CombatStrings = CombatStrings(),
    val commonUi: CommonUiStrings = CommonUiStrings(),
    val compendium: CompendiumStrings = CompendiumStrings(),
    val contact: ContactStrings = ContactStrings(),
    val character: CharacterStrings = CharacterStrings(),
    val characterCreation: CharacterCreationStrings = CharacterCreationStrings(),
    val characteristics: CharacteristicStrings = CharacteristicStrings(),
    val drawer: DrawerStrings = DrawerStrings(),
    val encounters: EncounterStrings = EncounterStrings(),
    val messages: MessageStrings = MessageStrings(),
    val miracles: MiracleStrings = MiracleStrings(),
    val npcs: NpcStrings = NpcStrings(),
    val permissions: PermissionStrings = PermissionStrings(),
    val points: PointStrings = PointStrings(),
    val parties: PartyStrings = PartyStrings(),
    val premium: PremiumStrings = PremiumStrings(),
    val races: RaceStrings = RaceStrings(),
    val settings: SettingsStrings = SettingsStrings(),
    val skills: SkillStrings = SkillStrings(),
    val spells: SpellStrings = SpellStrings(),
    val tests: TestStrings = TestStrings(),
    val talents: TalentStrings = TalentStrings(),
    val trappings: TrappingStrings = TrappingStrings(),
    val validation: ValidationStrings = ValidationStrings(),
    val weapons: WeaponStrings = WeaponStrings(),
)

@Immutable
data class AboutStrings(
    val appName: String = "WFRP Master",
    val attributionIcons: String = """
        Icons made by Delapouite and Lorc.
        Available on https://game-icons.net
    """.trimIndent(),
    val attributionDiceRollSound: String = "\"Shake And Roll Dice\" sound was made by Mike Koenig (soundbible.com) and used via CC BY 3.0 license",
    val body: String = """
        This application was created by František Maša and his student peers, now it's developed
        in free time by František Maša.
        If you like the app, please consider rating WFRP Master on Google Play.
    """.trimIndent(),
    val iconAppLogo: String = "Application logo",
    val title: String = "About",
    val titleAttribution: String = "Attribution",
)

@Immutable
data class AmbitionStrings(
    val labelLongTerm: String = "Long-term",
    val labelShortTerm: String = "Short-term",
    val titleCharacterAmbitions: String = "Character ambitions",
    val titlePartyAmbitions: String = "Party ambitions",
    val messages: AmbitionMessageStrings = AmbitionMessageStrings(),
)

data class AmbitionMessageStrings(
    val notFilled: String = "Ambition not filled",
)

@Immutable
data class AuthenticationString(
    val buttonSignIn: String = "Sign in",
    val labelEmail: String = "Email",
    val labelPassword: String = "Password",
    val messages: AuthenticationMessageStrings = AuthenticationMessageStrings(),
    val startupGoogleSignInFailed: String = """
        We could not sign you in via Google.
        Your data will be tied to this device, but you can always sign in later in Settings.
    """.trimIndent(),
)

@Immutable
data class AuthenticationMessageStrings(
    val duplicateAccount: String = "Existing account found",
    val emailNotFound: String = "Email not found",
    val invalidEmail: String = "Invalid email",
    val invalidPassword: String = "Invalid password",
    val unknownError: String = "Unknown error occurred",
    val googleAccountCollision: String = """
        There is already different WFRP Master account associated with given Google account.",
        Do you want to sign in to that account instead?
    """.trimIndent(),
    val loseAccessToParties: String = "You will lose access to these parties:",
    val notSignedInDescription: String = """
        You are not signed-in.
        Signing-in lets you keep access to parties between devices.
    """.trimIndent(),
    val signedInAs: String = "You are signed-in as",
)

@Immutable
data class ContactStrings(
    val bugReportEmailSubject: String = "Issue Report for WFRP Master",
    val googlePlayUrl: String = "https://play.google.com/store/apps/details?id=cz.frantisekmasa.dnd",
    val privacyPolicyUrl: String = "https://dnd-master-58fca.web.app/privacy-policy.html",
    val emailAddress: String = "frantisekmasa1+wfrp-master@gmail.com",
)

@Immutable
data class MessageStrings(
    val authenticationGoogleSignInFailed: String = "Google sign-in failed",
    val avatarChanged: String = "Avatar was changed",
    val avatarRemoved: String = "Avatar was removed",
    val couldNotOpenFile: String = "Could not open file",
    val errorUnknown: String = "Unknown error occurred",
    val invitationErrorInvitationInvalid: String = "Invitation link is not valid",
    val noInternet: String = "No internet connection",
    val partyRemoved: String = "Party was removed",
    val partyCreateErrorNoConnection: String = "You need to be connected to internet to create parties",
    val partyUpdated: String = "Party was updated",
    val partyUpdateErrorNoConnection: String = "You need to be connected to internet to update party",
    val nonDesktopFeature: String = "This feature is currently not available for Desktop version. We plan to introduce it in future versions.",
)

@Immutable
data class SettingsStrings(
    val darkMode: String = "Dark Mode",
    val personalizedAds: String = "Personalized Ads",
    val sound: String = "Sound Effects",
    val title: String = "Settings",
    val titleAccount: String = "Account",
    val titleGeneral: String = "General",
    val titlePremium: String = "Premium",
)

@Immutable
data class DrawerStrings(
    val privacyPolicy: String = "Privacy Policy",
    val rateApp: String = "Rate the App",
    val reportIssue: String = "Report an Issue",
)

@Immutable
data class EncounterStrings(
    val buttonAdd: String = "Add encounter",
    val labelName: String = "Name",
    val labelDescription: String = "Description (Optional)",
    val messages: EncounterMessageStrings = EncounterMessageStrings(),
    val titleCreate: String = "Create Encounter",
    val titleDescription: String = "Description",
    val titleEdit: String = "Edit Encounter",
)

@Immutable
data class EncounterMessageStrings(
    val noEncounters: String = "No encounters",
    val noEncountersSubtext: String = "Add first encounter for your party.",
    val removalConfirmation: String = "Do you really want to remove this encounter?",
)

@Immutable
data class ArmourStrings(
    val labelArmourPoints: String = "Armour Points (APs)",
    val labelType: String = "Armour Type",
    val labelLocations: String = "Locations",
    val locations: ArmourLocationStrings = ArmourLocationStrings(),
    val messages: ArmourMessageStrings = ArmourMessageStrings(),
    val title: String = "Armour",
    val types: ArmourTypeStrings = ArmourTypeStrings(),
)

@Immutable
data class ArmourMessageStrings(
    val atLeastOneLocationRequired: String = "At least one location is required",
)

@Immutable
data class ArmourLocationStrings(
    val shield: String = "Shield",
    val leftLeg: String = "Left Leg",
    val rightLeg: String = "Right Leg",
    val leftArm: String = "Left Arm",
    val rightArm: String = "Right Arm",
    val body: String = "Body",
    val head: String = "Head",
)

@Immutable
data class ArmourTypeStrings(
    val boiledLeather: String = "Boiled Leather",
    val mail: String = "Mail",
    val other: String = "Other",
    val plate: String = "Plate",
    val softLeather: String = "Soft Leather",
)

@Immutable
data class BlessingStrings(
    val buttonAddNonCompendium: String = "…or add non-Compendium blessing",
    val labelDuration: String = "Duration",
    val labelEffect: String = "Effect",
    val labelName: String = "Name",
    val labelRange: String = "Range",
    val labelTarget: String = "Target",
    val messages: BlessingMessageStrings = BlessingMessageStrings(),
    val title: String = "Blessings",
    val titleAdd: String = "Add Blessing",
    val titleChooseCompendiumBlessing: String = "Choose Compendium blessing…",
    val titleEdit: String = "Edit Blessing",
    val titleNew: String = "New Blessing",
)

@Immutable
data class BlessingMessageStrings(
    val noBlessingsInCompendium: String = "No blessings",
    val noBlessingsInCompendiumSubtext: String = "There are no blessings in your compendium yet.",
    val noBlessingsInCompendiumSubtextPlayer: String = "Your GM has to add them first.",
    val characterHasNoBlessings: String = "Character doesn't known any blessings.",
)

@Immutable
data class CharacterStrings(
    val buttonAdd: String = "Add Character",
    val labelCareer: String = "Career",
    val labelCharacteristicAdvances: String = "Advances",
    val labelCharacteristicBase: String = "Base",
    val labelClass: String = "Class",
    val labelMotivation: String = "Motivation (Optional)",
    val labelName: String = "Name",
    val labelNote: String = "Additional notes (Optional)",
    val labelPsychology: String = "Psychology (Optional)",
    val labelRace: String = "Race",
    val labelStatus: String = "Status",
    val motivation: String = "Motivation",
    val titleEdit: String = "Edit Character",
    val tabAttributes: String = "Attributes",
    val tabConditions: String = "Conditions",
    val tabReligions: String = "Religion",
    val tabSkills: String = "Skills",
    val tabSpells: String = "Spells",
    val tabTrappings: String = "Trappings",
    val titleCharacteristics: String = "Characteristics",
    val titleSelectCharacter: String = "Select Character",
)

@Immutable
data class CharacteristicStrings(
    val agility: String = "Agility",
    val ballisticSkill: String = "Ballistic Skill",
    val dexterity: String = "Dexterity",
    val fellowship: String = "Fellowship",
    val initiative: String = "Initiative",
    val intelligence: String = "Intelligence",
    val strength: String = "Strength",
    val toughness: String = "Toughness",
    val weaponSkill: String = "Weapon Skill",
    val willPower: String = "Will Power",
)

@Immutable
data class PointStrings(
    val autoMaxWoundsPlaceholder: String = "Auto",
    val corruption: String = "Corruption",
    val experience: String = "XP",
    val fate: String = "Fate",
    val fortune: String = "Fortune",
    val labelHardy: String = "Hardy (Increase maximum wounds by TB)",
    val labelCurrentExperience: String = "Current XP",
    val labelSinPoints: String = "Sin points",
    val labelSpentExperience: String = "Previously spent XP",
    val maxWounds: String = "Max. Wounds",
    val resilience: String = "Resilience",
    val resolve: String = "Resolve",
    val spentExperience: (points: Int) -> String = { "$it spent" },
    val wounds: String = "Wounds",
)

@Immutable
data class CalendarStrings(
    val iconPreviousMonth: String = "Previous Month",
    val iconPreviousYears: String = "Previous Years",
    val iconNextMonth: String = "Next Month",
    val iconNextYears: String = "Next Years",
    val moonPhases: MoonPhaseStrings = MoonPhaseStrings(),
    val mannsliebPhase: (String) -> String = { "Mannslieb phase: $it" },
    val titleSelectTime: String = "Select Time",
)

@Immutable
data class MoonPhaseStrings(
    val newMoon: String =    "New moon",
    val fullMoon: String = "Full moon",
    val waxing: String = "Waxing",
    val waning: String = "Waning",
)

@Immutable
data class CommonUiStrings(
    val boolean: (value: Boolean) -> String = { if (it) "Yes" else "No" },
    val buttonCancel: String = "Cancel",
    val buttonCreate: String = "Create",
    val buttonDetail: String = "Detail",
    val buttonDuplicate: String = "Duplicate",
    val buttonFinish: String = "Finish",
    val buttonOpen: String = "Open",
    val buttonOk: String = "Ok",
    val buttonRemove: String = "Remove",
    val buttonSave: String = "Save",
    val buttonSkip: String = "Skip",
    val labelCloseDialog: String = "Close current dialog",
    val labelExpandSelectBox: String = "Expand select box",
    val labelOpenDrawer: String = "Open navigation drawer",
    val labelOpenContextMenu: String = "Open context menu",
    val labelPreviousScreen: String = "Return to previous screen",
    val decrement: String = "Decrement value",
    val increment: String = "Increment value",
    val iconToggleFabMenu: String = "Toggle menu",
)

@Immutable
data class CombatStrings(
    val buttonEndCombat: String = "End combat",
    val iconNextTurn: String = "Next turn",
    val iconPreviousTurn: String = "Previous turn",
    val initiativeStrategyConfigOption: String = "Initiative rules",
    val initiativeStrategyPrompt: String = "Select Initiative rules",
    val initiativeStrategies: InitiativeStrategyStrings = InitiativeStrategyStrings(),
    val labelAdvantage: String = "Advantage",
    val messages: CombatMessageStrings = CombatMessageStrings(),
    val nthRound: (round: Int) -> String = { "Round $it" },
    val title: String = "Combat",
    val titleCharacterCombatants: String = "Characters",
    val titleStartCombat: String = "Start Combat",
    val titleNpcCombatants: String = "NPCs",
)

@Immutable
data class CombatMessageStrings(
    val combatInProgress: String = "Combat is in progress",
    val noActiveCombat: String = "There is no active combat",
)

@Immutable
data class CompendiumStrings(
    val assurance: String = "The file is not saved anywhere and never leaves your device.",
    val buttonBuy: String = "Buy",
    val buttonImport: String = "Import",
    val buttonImportRulebook: String = "Import rulebook PDF",
    val iconAddCompendiumItem: String = "Add compendium item",
    val importPrompt: String = "Import compendium from official WFRP rulebook.",
    val messages: CompendiumMessageStrings = CompendiumMessageStrings(),
    val pickPromptSkills: String = "Select which skills you want to import.",
    val pickPromptTalents: String = "Select which talents you want to import.",
    val pickPromptSpells: String = "Select which spells you want to import.",
    val pickPromptBlessings: String = "Select which blessings you want to import.",
    val pickPromptMiracles: String = "Select which miracles you want to import.",
    val rulebookStoreLink: String = "https://www.drivethrurpg.com/product/248284/Warhammer-Fantasy-Roleplay-Fourth-Edition-Rulebook?affiliate_id=2708720",
    val tabBlessings: String = "Blessings",
    val tabMiracles: String = "Miracles",
    val tabSkills: String = "Skills",
    val tabSpells: String = "Spells",
    val tabTalents: String = "Talents",
    val title: String = "Compendium",
    val titleImportCompendium: String = "Import Compendium",
    val titleImportDialog: String = "Importing Rulebook…",
)

@Immutable
data class CompendiumMessageStrings(
    val outOfMemory: String = "PDF import failed. Not enough available RAM on device.",
    val importFailed: String = "PDF import failed. Check that you provided valid rulebook PDF.",
    val itemAlreadyExists: String = "Item already exists",
)

@Immutable
data class InitiativeStrategyStrings(
    val initiativeCharacteristic: String = "Initiative, then Agility",
    val initiativeTest: String = "Opposed Initiative Test",
    val initiativePlus1d10: String = "Initiative + 1d10",
    val bonusesPlus1d10: String = "IB + AgiB + 1d10",
)

@Immutable
data class PartyStrings(
    val buttonInvite: String = "Invite",
    val buttonJoin: String = "Join party",
    val buttonLeave: String = "Leave",
    val buttonShareLink: String = "Share link",
    val imageQrCodeAlt: String = "Invitation QR code",
    val labelName: String = "Party Name",
    val messages: PartyMessageStrings = PartyMessageStrings(),
    val numberOfPlayers: (players: Int) -> String = { "$it players" },
    val tabCalendar: String = "Calendar",
    val tabCharacters: String = "Characters",
    val tabEncounters: String = "Encounters",
    val titleCreateParty: String = "Create Party",
    val titleCharacters: String = "Characters",
    val titleInvitePlayers: String = "Invite players…",
    val titleJoin: String = "Join Party",
    val titleJoinViaQrCode: String = "Join via QR code",
    val titleParties: String = "Parties",
    val titleRename: String = "Rename Party",
    val titleSettings: String = "Party Settings",
    val titleSettingsGeneral: String = "General",
)

@Immutable
data class PartyMessageStrings(
    val alreadyMember: String = "You are already member of this party",
    val leaveConfirmation: String = "Do you really want to leave this party?",
    val membersWillLoseAccess: String = "All members will lose access.",
    val noCharactersInParty: String = "No player has joined this party yet.",
    val noParties: String = "No parties",
    val noPartiesSubtext: String = """
        Create your first party
        or join the existing one.
    """.trimIndent(),
    val removalConfirmation: String = "Do you really want to remove this party?",
    val waitingForPlayerCharacter: String = "Waiting for player to create character",
    val qrCodeDescription: String = "Players can scan this QR code in their apps",
    val invitationUrlDescription: String = "Players can open this URL on their devices to gain access",
    val qrCodeScanningPrompt: String = "Scan the code provided by your GM",
    val cameraPermissionRequired: String = "Camera permission required",
    val invalidInvitationToken: String = "Invitation token is not valid",
    val invitationLinkAlternative: String = "Alternatively you can ask your GM for invitation link.",
    val invitationConfirmation: (partyName: String) -> AnnotatedString = {
        buildAnnotatedString {
            append("You have been invited to ")
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(it) }
            append(".")
        }
    }
)

@Immutable
data class PremiumStrings(
    val buttonUpgrade: String = "Upgrade",
    val dialogTitle: String = "Upgrade to Premium",
    val prompt: String = """
        Thank you for using WFRP Master!
        In free version you are limited to one Party and three Encounters.
        To join more Parties or create more Encounters, please upgrade to Premium version.
        
        If you have previously purchased Premium, make sure you are signed in as the same user in Settings.
    """.trimIndent()
)

@Immutable
data class ValidationStrings(
    val integer: String = "Must be number without decimals",
    val invalidExpression: String = "Expression is not valid",
    val notBlank: String = "Cannot be empty",
    val nonNegative: String = "Must be greater or equal to 0",
    val positiveInteger: String = "Must be positive number without decimals",
    val required: String = "Required",
)

@Immutable
data class WeaponStrings(
    val flaws: WeaponFlawStrings = WeaponFlawStrings(),
    val helperDamage: String = "Allowed operators: +,-,/,*,(,) and variables: SB",
    val helperRange: String = "Allowed operators: +,-,/,*,(,) and variables: SB",
    val helperAmmunitionRange: String = "Allowed operators: +,-,/,*,(,) and variables: WeaponDamage",
    val labelDamage: String = "Damage",
    val labelFlaws: String = "Weapon Flaws",
    val labelGroup: String = "Weapon Group",
    val labelQualities: String = "Weapon Qualities",
    val labelRange: String = "Range",
    val labelReach: String = "Reach",
    val meleeGroups: MeleeWeaponGroupStrings = MeleeWeaponGroupStrings(),
    val qualities: WeaponQualityStrings = WeaponQualityStrings(),
    val rangedGroups: RangedWeaponGroupStrings = RangedWeaponGroupStrings(),
    val reach: WeaponReachStrings = WeaponReachStrings(),
)

@Immutable
data class WeaponFlawStrings(
    val dangerous: String = "Dangerous",
    val imprecise: String = "Imprecise",
    val reload: String = "Reload",
    val slow: String = "Slow",
    val tiring: String = "Tiring",
    val undamaging: String = "Undamaging",
)

@Immutable
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

@Immutable
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

@Immutable
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

@Immutable
data class WeaponReachStrings(
    val personal: String = "Personal",
    val veryShort: String = "Very Short",
    val short: String = "Short",
    val average: String = "Average",
    val long: String = "Long",
    val veryLong: String = "Very Long",
    val massive: String = "Massive",
)

@Immutable
data class TrappingStrings(
    val iconEncumbrance: String = "Trapping encumbrance",
    val iconTotalEncumbrance: String = "Total encumbrance of character (current encumbrance / total encumbrance)",
    val labelCarries: String = "Carries",
    val labelName: String = "Name",
    val labelQuantity: String = "Quantity",
    val labelDescription: String = "Description (optional)",
    val labelEncumbrancePerUnit: String = "Encumbrance (per unit)",
    val labelWorn: String = "Worn",
    val labelType: String = "Trapping type",
    val messages: TrappingMessageStrings = TrappingMessageStrings(),
    val money: MoneyStrings = MoneyStrings(),
    val title: String = "Trappings",
    val titleAdd: String = "Add trapping",
    val titleEdit: String = "Edit trapping",
    val types: TrappingTypeStrings = TrappingTypeStrings(),
)

@Immutable
data class TrappingMessageStrings(
    val noItems: String = "There is no trapping in your inventory.",
)

@Immutable
data class MoneyStrings(
    val add: String = "Add",
    val balance: String = "Balance",
    val brassPenniesShortcut: String = "p",
    val crowns: String = "Crowns",
    val goldCoinsShortcut: String = "gc",
    val messages: MoneyMessageStrings = MoneyMessageStrings(),
    val pennies: String = "Pennies",
    val shillings: String = "Shillings",
    val subtract: String = "Subtract",
    val silverShillingsShortcut: String = "s",
    val titleNewTransaction: String = "New money transaction",
)

@Immutable
data class MoneyMessageStrings(
    val notEnoughMoney: String = "Character does not have enough money",
)

@Immutable
data class MiracleStrings(
    val buttonAddNonCompendium: String = "…or add non-Compendium miracle",
    val labelCultName: String = "Cult name",
    val labelDuration: String = "Duration",
    val labelEffect: String = "Effect",
    val labelName: String = "Name",
    val labelRange: String = "Range",
    val labelTarget: String = "Target",
    val title: String = "Miracles",
    val titleChooseCompendiumSpell: String = "Choose Compendium spell…",
    val titleAdd: String = "Add Miracle",
    val titleNew: String = "New Miracle",
    val titleEdit: String = "Edit Miracle",
    val messages: MiracleMessageStrings = MiracleMessageStrings(),
)

@Immutable
data class MiracleMessageStrings(
    val characterHasNoMiracles: String = "Character doesn't known any miracles.",
    val noMiraclesInCompendium: String = "No miracles",
    val noMiraclesInCompendiumSubtext: String = "There are no miracles in your compendium yet.",
    val noMiraclesInCompendiumSubtextPlayer: String = "Your GM has to add them first.",
)

@Immutable
data class TrappingTypeStrings(
    val ammunition: String = "Ammunition",
    val armour: String = "Armour",
    val container: String = "Container",
    val meleeWeapon: String = "Melee Weapon",
    val miscellaneous: String = "Miscellaneous",
    val rangedWeapon: String = "Ranged Weapon",
)

@Immutable
data class PermissionStrings(
    val buttonOpenSettings: String = "Open Settings",
    val buttonRequestPermission: String = "Request permission",
    val cameraDenied: String = "Camera permission required",
    val cameraRequired: String = "Camera permission required",
    val messages: PermissionMessageStrings = PermissionMessageStrings(),
)

data class PermissionMessageStrings(
    val cameraPermissionRationale: String = "Access to camera is needed to let you scan QR codes with party invitations.",
    val settingsScreenInstructions: String = "Please, grant us access on the Settings screen.",
)

@Immutable
data class RaceStrings(
    val dwarf: String = "Dwarf",
    val highElf : String = "High Elf",
    val woodElf : String = "Wood Elf",
    val halfling: String = "Halfling",
    val human: String = "Human",
    val gnome: String = "Gnome",
)

@Immutable
data class SkillStrings(
    val buttonAddNonCompendium: String = "…or add non-Compendium skill",
    val labelAdvanced: String = "Advanced",
    val labelAdvances: String = "Advances",
    val labelCharacteristic: String = "Characteristic",
    val labelDescription: String = "Description (optional)",
    val labelName: String = "Name",
    val messages: SkillMessageStrings = SkillMessageStrings(),
    val testNumberShortcut: String = "TN",
    val titleAdd: String = "Add Skill",
    val titleChooseCompendiumSkill: String = "Choose Compendium skill…",
    val titleEdit: String = "Edit Skill",
    val titleNew: String = "New Skill",
    val titleSkills: String = "Skills",
    val titleSelectSkill: String = "Select Skill",
)

@Immutable
data class SkillMessageStrings(
    val characterHasNoSkills: String = "Character doesn't have any skills.",
    val compendiumSkillRemoved: String = "Compendium skill was removed in the meantime",
    val noSkillsInCompendium: String = "No skills",
    val noSkillsInCompendiumSubtext: String = "There are no skills in your compendium yet.",
    val noSkillsInCompendiumSubtextPlayer: String = "Your GM has to add them first.",
)

@Immutable
data class SpellStrings(
    val buttonAddNonCompendium: String = "…or add non-Compendium spell",
    val castingNumberShortcut: String = "CN",
    val labelCastingNumber: String = "Casting Number (CN)",
    val labelDuration: String = "Duration",
    val labelEffect: String = "Effect",
    val labelLore: String = "Lore",
    val labelMemorized: String = "Memorized",
    val labelName: String = "Name",
    val labelRange: String = "Range",
    val labelTarget: String = "Target",
    val messages: SpellMessageStrings = SpellMessageStrings(),
    val titleAdd: String = "Add Spell",
    val titleChooseCompendiumSpell: String = "Choose Compendium spell…",
    val titleEdit: String = "Edit Spell",
    val titleNew: String = "New Spell",
)

@Immutable
data class SpellMessageStrings(
    val noSpellsInCompendium: String = "No spells",
    val noSpellsInCompendiumSubtext: String = "There are no spells in your compendium yet.",
    val noSpellsInCompendiumSubtextPlayer: String = "Your GM has to add them first.",
    val characterHasNoSpell: String = "No spells",
    val characterHasNoSpellSubtext: String = "You don't know any spells.",
)

@Immutable
data class NpcStrings(
    val buttonAddNpc: String = "New NPC",
    val labelAlive: String = "Alive",
    val labelDescription: String = "Description (Optional)",
    val labelEnemy: String = "Enemy",
    val labelName: String = "Name",
    val messages: NpcMessages = NpcMessages(),
    val title: String = "NPC",
    val titlePlural: String = "NPCs",
    val titleAdd: String = "New NPC",
    val titleCharacteristics: String = "Characteristics",
    val titleArmour: String = "Armour",
)

@Immutable
data class NpcMessages(
    val noNpcs: String = "There are no NPCs yet. Add first one.",
)

@Immutable
data class TalentStrings(
    val buttonAddNonCompendium: String = "…or add non-Compendium talent",
    val labelDescription: String = "Description (Optional)",
    val labelName: String = "Name",
    val labelMaxTimesTaken: String = "Max",
    val labelTimesTaken: String = "Times taken",
    val messages: TalentMessageStrings = TalentMessageStrings(),
    val titleChooseCompendiumTalent: String = "Choose Compendium talent…",
    val titleAdd: String = "Add Talent",
    val titleNew: String = "New Talent",
    val titleEdit: String = "Edit Talent",
    val titleTalents: String = "Talents",
)

@Immutable
data class TalentMessageStrings(
    val noTalentsInCompendium: String = "No talents",
    val noTalentsInCompendiumSubtext: String = "There are no talents in your compendium yet.",
    val noTalentsInCompendiumSubtextPlayer: String = "Your GM has to add them first.",
    val compendiumTalentRemoved: String = "Compendium talent was removed in the meantime",
)

@Immutable
data class TestStrings(
    val buttonExecute: String = "Execute",
    val buttonHiddenSkillTest: String = "Hidden skill test",
    val buttonReroll: String = "Reroll",
    val cannotTestAgainstUnknownAdvancedSkill: String = "Character cannot test against advanced skill (s)he does not know.",
    val critical: String = "Critical",
    val difficulties: TestDifficultyStrings = TestDifficultyStrings(),
    val fumble: String = "Fumble",
    val labelDifficulty: String = "Difficulty",
    val labelDramaticResult: String = "Dramatic Result",
    val labelSkill: String = "Skill",
    val roll: String = "Roll",
    val rollLabel: (rollValue: Int, testedValue: Int) -> String = { r, t -> "$r vs $t" },
    val results: TestResultStrings = TestResultStrings(),
    val successLevelShortcut: String = "SL",
    val titleSkillTest: String = "Perform skill test",
    val titleTest: (skillName: String) -> String = { "$it Test" },
)

@Immutable
data class TestDifficultyStrings(
    val veryEasy: String = "Very Easy",
    val easy: String = "Easy",
    val average: String = "Average",
    val challenging: String = "Challenging",
    val difficult: String = "Difficult",
    val hard: String = "Hard",
    val veryHard: String = "Very Hard",
)

@Immutable
data class TestResultStrings(
    val astoundingSuccess : String = "Astounding Success",
    val impressiveSuccess : String = "Impressive Success",
    val success: String = "Success",
    val marginalSuccess : String = "Marginal Success",
    val marginalFailure : String = "Marginal Failure",
    val failure: String = "Failure",
    val impressiveFailure : String = "Impressive Failure",
    val astoundingFailure : String = "Astounding Failure",
)

@Immutable
data class CharacterCreationStrings(
    val stepAttributes: String = "Attributes",
    val stepBasicInfo: String = "Basic info",
    val stepPointPools: String = "Point pools",
    val title: String = "Describe your character…",
)
