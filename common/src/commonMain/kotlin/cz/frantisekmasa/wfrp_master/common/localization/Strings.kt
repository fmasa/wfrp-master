package cz.frantisekmasa.wfrp_master.common.localization

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.ArmourPoints

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
    val careers: CareerStrings = CareerStrings(),
    val changelog: ChangelogStrings = ChangelogStrings(),
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
    val socialClasses: SocialClassStrings = SocialClassStrings(),
    val tests: TestStrings = TestStrings(),
    val talents: TalentStrings = TalentStrings(),
    val traits: TraitStrings = TraitStrings(),
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
    val searchNotFound: String = "No results",
    val searchNotFoundSubtext: String = "Consider changing the search phrase.",
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
    val wiki: String = "Wiki",
)

@Immutable
data class EncounterStrings(
    val buttonAdd: String = "Add encounter",
    val buttonShowCompleted: String = "Show Completed",
    val labelName: String = "Name",
    val labelCompleted: String = "Completed",
    val labelDescription: String = "Description (Optional)",
    val messages: EncounterMessageStrings = EncounterMessageStrings(),
    val title: String = "Encounters",
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
    val flaws: ArmourFlawStrings = ArmourFlawStrings(),
    val labelArmourPoints: String = "Armour Points (AP)",
    val labelFlaws: String = "Armour Flaws",
    val labelLocations: String = "Locations",
    val labelQualities: String = "Armour Qualities",
    val labelType: String = "Armour Type",
    val messages: ArmourMessageStrings = ArmourMessageStrings(),
    val points: (ArmourPoints) -> String = { "${it.value} AP" },
    val qualities: ArmourQualityStrings = ArmourQualityStrings(),
    val shield: String = "Shield",
    val tipTrappings: String = "Armour is auto-calculated from worn armour trappings.",
    val title: String = "Armour",
    val types: ArmourTypeStrings = ArmourTypeStrings(),
)

@Immutable
data class ArmourFlawStrings(
    val partial: String = "Partial",
    val weakpoints: String = "Weakpoints",
)

@Immutable
data class ArmourMessageStrings(
    val atLeastOneLocationRequired: String = "At least one location is required",
)

@Immutable
data class ArmourQualityStrings(
    val flexible: String = "Flexible",
    val impenetrable: String = "Impenetrable",
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
data class CareerStrings(
    val buttonClearSelectBox: String = "Clear Career Select box",
    val labelName: String = "Name",
    val labelCharacteristics: String = "Characteristics",
    val labelDescription: String = "Description (Optional)",
    val labelRaces: String = "Races",
    val labelSkills: String = "Other Skills",
    val labelIncomeSkills: String = "Income Skills",
    val labelTalents: String = "Talents",
    val labelTrappings: String = "Trappings",
    val labelSocialClass: String = "Social class",
    val tabDetail: String = "Detail",
    val tabLevels: String = "Levels",
    val titleAddLevel: String = "Add Level",
    val titleEditCareer: String = "Edit Career",
    val titleEditLevel: String = "Edit Level",
    val titleNewCareer: String = "New Career…",
    val messages: CareerMessageStrings = CareerMessageStrings(),
    val commaSeparatedSkillsHelper: String = "Comma separated list of Skills",
    val commaSeparatedTalentsHelper: String = "Comma separated list of Talents",
    val commaSeparatedTrappingsHelper: String = "Comma separated list of Trappings",
    val searchPlaceholder: String = "Search in Careers",
)

@Immutable
data class CareerMessageStrings(
    val noCareersInCompendium: String = "No careers",
    val noCareersInCompendiumSubtext: String = "There are no careers in your compendium yet.",
    val notFound: String = "Career not found",
    val noLevel: String = "No career levels",
    val noLevelSubtext: String = "Create at least one level\nto let Characters use this career.",
    val levelWithNameExists: String = "Career level with same name already exists",
)

@Immutable
data class CharacterStrings(
    val buttonAdd: String = "Add Character",
    val buttonChangeAvatar: String = "Change Avatar",
    val buttonRemoveAvatar: String = "Remove Avatar",
    val errorVisibleTabRequired: String = "At least one character tab must be visible",
    val labelCareer: String = "Career",
    val labelCharacteristicAdvances: String = "Advances",
    val labelCharacteristicBase: String = "Base",
    val labelClass: String = "Class",
    val labelCustomCareer: String = "Custom Career",
    val labelMotivation: String = "Motivation (Optional)",
    val labelName: String = "Name",
    val labelNote: String = "Additional notes (Optional)",
    val note: String = "Additional notes",
    val labelPsychology: String = "Psychology (Optional)",
    val labelPublicName: String = "Name visible to players",
    val labelRace: String = "Race",
    val labelStatus: String = "Status",
    val motivation: String = "Motivation",
    val messages: CharacterMessageStrings = CharacterMessageStrings(),
    val secondaryTextBasics: String = "Name, Race, Motivation",
    val secondaryTextCareer: String = "Career, Class, Social status",
    val secondaryTextExperience: String = "XP, Ambitions",
    val secondaryTextWellBeing: String = "Corruption, Injuries, Diseases, Psychology",
    val secondaryTextWounds: String = "Max Wounds, Hardy talent advances",
    val secondaryTextRemoval: String = "Permanently remove the Character",
    val tabsVisible: (visible: Int, total: Int) -> String = { visible, total ->
        "$visible of $total tabs visible"
    },
    val tabAttributes: String = "Attributes",
    val tabCombat: String = "Combat",
    val tabConditions: String = "Conditions",
    val tabNotes: String = "Notes",
    val tabReligions: String = "Religion",
    val tabSkillsAndTalents: String = "Skills & Talents",
    val tabSpells: String = "Spells",
    val tabTrappings: String = "Trappings",
    val titleBasics: String = "Basics",
    val titleCareer: String = "Career",
    val titleCharacteristics: String = "Characteristics",
    val titleEdit: String = "Edit Character",
    val titleExperience: String = "Experience",
    val titleGeneralSettings: String = "General",
    val titleRemoval: String = "Remove Character",
    val titleSelectCharacter: String = "Select Character",
    val titleUiSettings: String = "UI settings",
    val titleVisibleTabs: String = "Visible tabs",
    val titleWellBeing: String = "Well-being",
    val titleWeapons: String = "Weapons",
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
    val toughnessBonusShortcut: String = "TB",
    val weaponSkill: String = "Weapon Skill",
    val willPower: String = "Will Power",
)

@Immutable
data class CharacterMessageStrings(
    val characterRemoved: String = "Character has been removed.",
    val noEquippedWeapons: String = "No equipped weapons",
    val noEquippedWeaponsSubText: String = "Character does not have any Weapon trappings equipped",
    val removalDialogText: (characterName: String) -> AnnotatedString = { characterName ->
        buildAnnotatedString {
            append("Do you really want to permanently remove ")
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(characterName) }
            append("?")
        }
    }
)

@Immutable
data class PointStrings(
    val autoMaxWoundsPlaceholder: String = "Auto",
    val corruption: String = "Corruption points",
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
data class ChangelogStrings(
    val title: String = "Changelog",
    val couldNotLoad: String = "Changelog could not be loaded",
    val githubButton: String = "See rest on GitHub",
)

@Immutable
data class MoonPhaseStrings(
    val newMoon: String = "New moon",
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
    val buttonDismiss: String = "Dismiss",
    val buttonDuplicate: String = "Duplicate",
    val buttonEdit: String = "Edit",
    val buttonFinish: String = "Finish",
    val buttonKeep: String = "Keep",
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
    val itemNone: String = "None",
    val iconToggleFabMenu: String = "Toggle menu",
    val dismissTipConfirmation: String = "Do you really want to dismiss this tip?",
    val search: String = "Search",
    val expressionHelper: (variables: List<String>) -> String = {
        "Allowed operators: +,-,/,*,(,), MIN(...), MAX(...) and variables: " + it.joinToString(", ")
    }
)

@Immutable
data class CombatStrings(
    val advantageCap: String = "Advantage Cap",
    val advantageUnlimited: String = "Unlimited",
    val advantageSystemConfigOption: String = "Advantage rules",
    val advantageSystemPrompt: String = "Select Advantage rules",
    val advantageSystems: AdvantageSystemStrings = AdvantageSystemStrings(),
    val buttonEndCombat: String = "End combat",
    val buttonRemoveCombatant: String = "Remove from combat",
    val iconNextTurn: String = "Next turn",
    val hitLocations: HitLocationStrings = HitLocationStrings(),
    val iconPreviousTurn: String = "Previous turn",
    val initiativeStrategyConfigOption: String = "Initiative rules",
    val initiativeStrategyPrompt: String = "Select Initiative rules",
    val initiativeStrategies: InitiativeStrategyStrings = InitiativeStrategyStrings(),
    val labelAdvantage: String = "Advantage",
    val labelAllies: String = "Allies",
    val labelEnemies: String = "Enemies",
    val messages: CombatMessageStrings = CombatMessageStrings(),
    val nthRound: (round: Int) -> String = { "Round $it" },
    val title: String = "Combat",
    val titleCharacterCombatants: String = "Characters",
    val titleStartCombat: String = "Start Combat",
    val titleNpcCombatants: String = "NPCs",
)

@Immutable
data class AdvantageSystemStrings(
    val groupAdvantage: String = "Group Advantage (Up in Arms)",
    val coreRulebook: String = "Default (Core Rulebook)",
)

@Immutable
data class CombatMessageStrings(
    val combatInProgress: String = "Combat is in progress",
    val noConditions: String = "No Conditions",
    val waitingForCombat: String = "Waiting for Combat…",
    val doesNotApplyToGroupAdvantage: String = "Does not apply to Group Advantage",
)

@Immutable
data class HitLocationStrings(
    val leftLeg: String = "Left Leg",
    val rightLeg: String = "Right Leg",
    val leftArm: String = "Left Arm",
    val rightArm: String = "Right Arm",
    val body: String = "Body",
    val head: String = "Head",
)

@Immutable
data class CompendiumStrings(
    val assurance: String = "The file is not saved anywhere and never leaves your device.",
    val searchPlaceholder: String = "Search items",
    val bookCoreRulebook: String = "WFRP Core Rulebook",
    val bookUpInArms: String = "Up in Arms",
    val bookWindsOfMagic: String = "Winds of Magic",
    val buttonBuy: String = "Buy",
    val buttonImport: String = "Import",
    val buttonImportFromRulebook: String = "Import from Rulebook",
    val buttonImportFile: String = "Import file",
    val buttonImportRulebook: String = "Import rulebook PDF",
    val buttonExport: String = "Save Export",
    val buttonExportFile: String = "Export file",
    val iconAddCompendiumItem: String = "Add compendium item",
    val rulebookImportPrompt: AnnotatedString = buildAnnotatedString {
        append("Import compendium from official WFRP Rulebook PDFs.\n")
        withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
            append("Only the latest version of English Rulebooks are supported.")
        }
    },
    val jsonImportPrompt: AnnotatedString = buildAnnotatedString {
        append("Import compendium previously exported from WFRP Master.\n")
        withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
            append("The export format is not set in stone yet!\n")
        }
        append("so while exports from the same version of WFRP Master will work,")
        append("exports from previous versions may not work properly.")
    },
    val messages: CompendiumMessageStrings = CompendiumMessageStrings(),
    val pickPromptCareers: String = "Select which careers you want to import.",
    val pickPromptSkills: String = "Select which skills you want to import.",
    val pickPromptTalents: String = "Select which talents you want to import.",
    val pickPromptSpells: String = "Select which spells you want to import.",
    val pickPromptBlessings: String = "Select which blessings you want to import.",
    val pickPromptMiracles: String = "Select which miracles you want to import.",
    val pickPromptTraits: String = "Select which traits you want to import.",
    val rulebookStoreLink: String = "https://www.drivethrurpg.com/product/248284/Warhammer-Fantasy-Roleplay-Fourth-Edition-Rulebook?affiliate_id=2708720",
    val tabBlessings: String = "Blessings",
    val tabCareers: String = "Careers",
    val tabMiracles: String = "Miracles",
    val tabSkills: String = "Skills",
    val tabSpells: String = "Spells",
    val tabTalents: String = "Talents",
    val tabTraits: String = "Traits",
    val title: String = "Compendium",
    val titleImportCompendium: String = "Import Compendium",
    val titleExportCompendium: String = "Export Compendium",
    val titleImportDialog: String = "Importing Rulebook…",
)

@Immutable
data class CompendiumMessageStrings(
    val exportFailed: String = "JSON export failed.",
    val outOfMemory: String = "PDF import failed. Not enough available RAM on device.",
    val rulebookImportFailed: String = "PDF import failed. Check that you provided valid rulebook PDF.",
    val jsonImportFailed: String = "JSON import failed. Check that you provided valid WFRP Master export.",
    val itemAlreadyExists: String = "Item already exists",
    val itemDoesNotExist: String = "Item does not exist.",
    val noItems: String = "No items in compendium",
    val noItemsInCompendiumSubtextPlayer: String = "Your GM has to add them first.",
    val willReplaceExistingItem: String = "Will replace existing item",
)

@Immutable
data class InitiativeStrategyStrings(
    val initiativeCharacteristic: String = "Initiative, then Agility",
    val initiativeTest: String = "Opposed Initiative Test",
    val initiativePlus1d10: String = "Initiative + 1d10",
    val bonusesPlus1d10: String = "IB + AgB + 1d10",
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
    val tabCharacters: String = "Characters",
    val tabWorld: String = "World",
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
    val compendiumCardMoved: String = "Compendium card has been moved to the World tab.",
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
    },
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
    val equip: WeaponEquipStrings = WeaponEquipStrings(),
    val flaws: WeaponFlawStrings = WeaponFlawStrings(),
    val labelDamage: String = "Damage",
    val labelFlaws: String = "Weapon Flaws",
    val labelGroup: String = "Weapon Group",
    val labelGroups: String = "Weapon Groups",
    val labelQualities: String = "Weapon Qualities",
    val labelRange: String = "Range",
    val labelReach: String = "Reach",
    val labelEquip: String = "Equipped",
    val meleeGroups: MeleeWeaponGroupStrings = MeleeWeaponGroupStrings(),
    val qualities: WeaponQualityStrings = WeaponQualityStrings(),
    val rangedGroups: RangedWeaponGroupStrings = RangedWeaponGroupStrings(),
    val reach: WeaponReachStrings = WeaponReachStrings(),
)

@Immutable
data class WeaponEquipStrings(
    val primaryHand: String = "Primary hand",
    val offHand: String = "Off-hand",
    val bothHands: String = "Both hands",
    val notEquipped: String = "Not equipped",
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
    val buttonCreateNew: String = "Create new Trapping",
    val buttonSelectExisting: String = "Select existing Trapping",
    val buttonMoveToContainer: String = "Move to Container",
    val buttonTakeOut: String = "Take out",
    val iconEncumbrance: String = "Trapping encumbrance",
    val iconTotalEncumbrance: String = "Total encumbrance of character (current encumbrance / total encumbrance)",
    val labelCarries: String = "Carries",
    val labelFlaws: String = "Flaws",
    val labelName: String = "Name",
    val labelQualities: String = "Qualities",
    val labelQuantity: String = "Quantity",
    val labelDescription: String = "Description (optional)",
    val labelEncumbrancePerUnit: String = "Encumbrance (per unit)",
    val labelEncumbranceTotal: String = "Encumbrance (total)",
    val labelWorn: String = "Worn",
    val labelType: String = "Trapping type",
    val messages: TrappingMessageStrings = TrappingMessageStrings(),
    val money: MoneyStrings = MoneyStrings(),
    val none: String = "None",
    val searchPlaceholder: String = "Search in trappings…",
    val takeOut: String = "Take out",
    val title: String = "Trappings",
    val titleAdd: String = "Add trapping",
    val titleEdit: String = "Edit trapping",
    val titleEquipWeapon: String = "Equip Weapon",
    val titleSelectContainer: String = "Select Container",
    val titleSelectTrapping: String = "Select Trapping",
    val titleStoredTrappings: String = "Stored Trappings",
    val types: TrappingTypeStrings = TrappingTypeStrings(),
)

@Immutable
data class TrappingMessageStrings(
    val noItems: String = "There is no trapping in your inventory.",
    val trappingNotFound: String = "Trapping does not exist",
    val noItemsInContainer: String = "There are no trappings in this container.",
    val noTrappingsToAdd: String = "There are no trappings to add to container.",
    val noContainersFound: String = "There are no available containers.",
    val cannotStoreTrappingsInContainerStoredInContainer: String =
        "Trappings cannot be stored in Container that is itself stored in a Container."
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
    val titleChooseCompendiumMiracle: String = "Choose Compendium miracle…",
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
    val bookOrDocument: String = "Books and Documents",
    val clothingOrAccessory: String = "Clothing and Accessories",
    val container: String = "Containers",
    val drugOrPoison: String = "Drugs and Poisons",
    val foodOrDrink: String = "Food and Drink",
    val herbOrDraught: String = "Herbs and Draughts",
    val meleeWeapon: String = "Melee Weapons",
    val miscellaneous: String = "Miscellaneous",
    val rangedWeapon: String = "Ranged Weapons",
    val spellIngredient: String = "Spell Ingredients",
    val toolOrKit: String = "Tools and Kits",
    val tradeTools: String = "Trade tools",
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
    val custom: String = "Custom",
    val dwarf: String = "Dwarf",
    val highElf: String = "High Elf",
    val woodElf: String = "Wood Elf",
    val halfling: String = "Halfling",
    val human: String = "Human",
    val gnome: String = "Gnome",
    val ogre: String = "Ogre",
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

data class SocialClassStrings(
    val academics: String = "Academics",
    val burghers: String = "Burghers",
    val courtiers: String = "Courtiers",
    val peasants: String = "Peasants",
    val rangers: String = "Rangers",
    val riverfolk: String = "Riverfolk",
    val rogues: String = "Rogues",
    val warriors: String = "Warriors",
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
    val titleSpells: String = "Spells",
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
    val buttonAddNpc: String = "Add NPC",
    val labelAlive: String = "Alive",
    val labelCount: String = "Count",
    val labelDescription: String = "Description (Optional)",
    val labelEnemy: String = "Enemy",
    val labelName: String = "Name",
    val messages: NpcMessages = NpcMessages(),
    val searchPlaceholder: String = "Search in NPCs…",
    val title: String = "NPC",
    val titlePlural: String = "NPCs",
    val titleAdd: String = "Add NPC",
    val titleCharacteristics: String = "Characteristics",
    val titleArmour: String = "Armour",
)

@Immutable
data class NpcMessages(
    val noNpcs: String = "No NPCs",
    val noNpcsSubtext: String = "There are no NPCs yet. Add first one.",
    val noNpcsSearched: String = "No NPCs found",
    val noNpcsSearchedSubtext: String = "Consider changing the search phrase",
    val removalConfirmation: String = "Do you really want to permanently remove this NPC?",
)

@Immutable
data class TalentStrings(
    val buttonAddNonCompendium: String = "…or add non-Compendium talent",
    val labelTests: String = "Tests",
    val labelDescription: String = "Description (Optional)",
    val labelName: String = "Name",
    val labelMaxTimesTaken: String = "Max",
    val labelTimesTaken: String = "Times taken",
    val messages: TalentMessageStrings = TalentMessageStrings(),
    val tipHardyTalentCheckbox: String = "Hardy checkbox will be removed in the future.\n" +
        "Add Talent called \"Hardy\" and delete manual Max. Wounds value instead.",
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
    val astoundingSuccess: String = "Astounding Success",
    val impressiveSuccess: String = "Impressive Success",
    val success: String = "Success",
    val marginalSuccess: String = "Marginal Success",
    val marginalFailure: String = "Marginal Failure",
    val failure: String = "Failure",
    val impressiveFailure: String = "Impressive Failure",
    val astoundingFailure: String = "Astounding Failure",
)

@Immutable
data class TraitStrings(
    val labelDescription: String = "Description",
    val labelName: String = "Name",
    val labelSpecifications: String = "Specifications",
    val messages: TraitMessageStrings = TraitMessageStrings(),
    val specificationsHelper: String = "Comma separated list of Specifications",
    val titleChooseCompendiumTrait: String = "Choose Compendium trait…",
    val titleAdd: String = "Add Trait",
    val titleNew: String = "New Trait",
    val titleEdit: String = "Edit Trait",
    val titleTraits: String = "Traits",
)

@Immutable
data class TraitMessageStrings(
    val noTraitsInCompendium: String = "No traits",
    val noTraitsInCompendiumSubtext: String = "There are no traits in your compendium yet.",
    val compendiumTraitRemoved: String = "Compendium trait was removed in the meantime",
)

@Immutable
data class CharacterCreationStrings(
    val stepAttributes: String = "Attributes",
    val stepBasicInfo: String = "Basic info",
    val stepPointPools: String = "Point pools",
    val title: String = "Describe your character…",
    val messages: CharacterCreationMessageStrings = CharacterCreationMessageStrings(),
)

@Immutable
data class CharacterCreationMessageStrings(
    val noCareersInCompendium: String = "No careers",
)
