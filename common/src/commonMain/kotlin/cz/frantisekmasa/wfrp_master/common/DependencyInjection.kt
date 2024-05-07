package cz.frantisekmasa.wfrp_master.common

import cz.frantisekmasa.wfrp_master.common.changelog.ChangelogScreenModel
import cz.frantisekmasa.wfrp_master.common.character.CharacterDetailScreenModel
import cz.frantisekmasa.wfrp_master.common.character.CharacterPickerScreenModel
import cz.frantisekmasa.wfrp_master.common.character.CharacterScreenModel
import cz.frantisekmasa.wfrp_master.common.character.characteristics.CharacteristicsScreenModel
import cz.frantisekmasa.wfrp_master.common.character.combat.CharacterCombatScreenModel
import cz.frantisekmasa.wfrp_master.common.character.effects.EffectManager
import cz.frantisekmasa.wfrp_master.common.character.items.AvailableCompendiumItemsFactory
import cz.frantisekmasa.wfrp_master.common.character.religion.blessings.CharacterBlessingDetailScreenModel
import cz.frantisekmasa.wfrp_master.common.character.religion.blessings.add.AddBlessingScreenModel
import cz.frantisekmasa.wfrp_master.common.character.religion.miracles.CharacterMiracleDetailScreenModel
import cz.frantisekmasa.wfrp_master.common.character.religion.miracles.add.AddMiracleScreenModel
import cz.frantisekmasa.wfrp_master.common.character.skills.CharacterSkillDetailScreenModel
import cz.frantisekmasa.wfrp_master.common.character.skills.add.AddSkillScreenModel
import cz.frantisekmasa.wfrp_master.common.character.skills.addBasic.AddBasicSkillsScreenModel
import cz.frantisekmasa.wfrp_master.common.character.spells.CharacterSpellDetailScreenModel
import cz.frantisekmasa.wfrp_master.common.character.spells.add.AddSpellScreenModel
import cz.frantisekmasa.wfrp_master.common.character.talents.CharacterTalentDetailScreenModel
import cz.frantisekmasa.wfrp_master.common.character.talents.add.AddTalentScreenModel
import cz.frantisekmasa.wfrp_master.common.character.traits.CharacterTraitDetailScreenModel
import cz.frantisekmasa.wfrp_master.common.character.traits.add.AddTraitScreenModel
import cz.frantisekmasa.wfrp_master.common.character.trappings.CharacterTrappingsDetailScreenModel
import cz.frantisekmasa.wfrp_master.common.character.trappings.TrappingSaver
import cz.frantisekmasa.wfrp_master.common.character.trappings.add.AddTrappingScreenModel
import cz.frantisekmasa.wfrp_master.common.characterCreation.CharacterCreationScreenModel
import cz.frantisekmasa.wfrp_master.common.combat.CombatScreenModel
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumExportScreenModel
import cz.frantisekmasa.wfrp_master.common.compendium.blessing.BlessingCompendiumScreenModel
import cz.frantisekmasa.wfrp_master.common.compendium.career.CareerCompendiumScreenModel
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Blessing
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Career
import cz.frantisekmasa.wfrp_master.common.compendium.domain.CompendiumItem
import cz.frantisekmasa.wfrp_master.common.compendium.domain.JournalEntry
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Miracle
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Skill
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Spell
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Talent
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trait
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trapping
import cz.frantisekmasa.wfrp_master.common.compendium.journal.JournalScreenModel
import cz.frantisekmasa.wfrp_master.common.compendium.miracle.MiracleCompendiumScreenModel
import cz.frantisekmasa.wfrp_master.common.compendium.skill.SkillCompendiumScreenModel
import cz.frantisekmasa.wfrp_master.common.compendium.spell.SpellCompendiumScreenModel
import cz.frantisekmasa.wfrp_master.common.compendium.talent.TalentCompendiumScreenModel
import cz.frantisekmasa.wfrp_master.common.compendium.trait.TraitCompendiumScreenModel
import cz.frantisekmasa.wfrp_master.common.compendium.trapping.TrappingCompendiumScreenModel
import cz.frantisekmasa.wfrp_master.common.core.PartyScreenModel
import cz.frantisekmasa.wfrp_master.common.core.cache.CharacterRepositoryIdentityMap
import cz.frantisekmasa.wfrp_master.common.core.cache.PartyRepositoryIdentityMap
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterAvatarChanger
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItem
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItemRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.FirestoreCompendium
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.EncounterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.religion.BlessingRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.religion.MiracleRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.skills.SkillRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.spells.SpellRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.talents.TalentRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.traits.TraitRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItemRepository
import cz.frantisekmasa.wfrp_master.common.core.firebase.Schema
import cz.frantisekmasa.wfrp_master.common.core.firebase.functions.CloudFunctionCharacterAvatarChanger
import cz.frantisekmasa.wfrp_master.common.core.firebase.repositories.FirestoreCharacterItemRepository
import cz.frantisekmasa.wfrp_master.common.core.firebase.repositories.FirestoreCharacterRepository
import cz.frantisekmasa.wfrp_master.common.core.firebase.repositories.FirestoreEncounterRepository
import cz.frantisekmasa.wfrp_master.common.core.firebase.repositories.FirestorePartyRepository
import cz.frantisekmasa.wfrp_master.common.core.serialization.UuidSerializer
import cz.frantisekmasa.wfrp_master.common.core.tips.DismissedUserTipsHolder
import cz.frantisekmasa.wfrp_master.common.encounters.EncounterDetailScreenModel
import cz.frantisekmasa.wfrp_master.common.encounters.EncountersScreenModel
import cz.frantisekmasa.wfrp_master.common.encounters.domain.EncounterRepository
import cz.frantisekmasa.wfrp_master.common.gameMaster.GameMasterScreenModel
import cz.frantisekmasa.wfrp_master.common.invitation.InvitationScreenModel
import cz.frantisekmasa.wfrp_master.common.invitation.domain.FirestoreInvitationProcessor
import cz.frantisekmasa.wfrp_master.common.invitation.domain.InvitationProcessor
import cz.frantisekmasa.wfrp_master.common.npcs.NpcsScreenModel
import cz.frantisekmasa.wfrp_master.common.partyList.PartyListScreenModel
import cz.frantisekmasa.wfrp_master.common.partySettings.PartySettingsScreenModel
import cz.frantisekmasa.wfrp_master.common.settings.SettingsScreenModel
import cz.frantisekmasa.wfrp_master.common.skillTest.SkillTestScreenModel
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.functions.functions
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlinx.serialization.serializer
import org.kodein.di.DI
import org.kodein.di.DirectDI
import org.kodein.di.bindFactory
import org.kodein.di.bindProvider
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import kotlin.random.Random

val appModule =
    DI.Module("Common") {
        import(platformModule)

        bindSingleton {
            Json {
                encodeDefaults = true
                serializersModule =
                    SerializersModule {
                        contextual(UuidSerializer())
                    }
            }
        }

        bindSingleton { Firebase.auth }

        bindSingleton<Compendium<Skill>> {
            FirestoreCompendium(Schema.Compendium.SKILLS, instance(), serializer())
        }

        bindSingleton<Compendium<Talent>> {
            FirestoreCompendium(Schema.Compendium.TALENTS, instance(), serializer())
        }

        bindSingleton<Compendium<Spell>> {
            FirestoreCompendium(Schema.Compendium.SPELLS, instance(), serializer())
        }

        bindSingleton<Compendium<Blessing>> {
            FirestoreCompendium(Schema.Compendium.BLESSINGS, instance(), serializer())
        }

        bindSingleton<Compendium<Miracle>> {
            FirestoreCompendium(Schema.Compendium.MIRACLES, instance(), serializer())
        }

        bindSingleton<Compendium<Trait>> {
            FirestoreCompendium(Schema.Compendium.TRAITS, instance(), serializer())
        }

        bindSingleton<Compendium<Career>> {
            FirestoreCompendium(Schema.Compendium.CAREERS, instance(), serializer())
        }

        bindSingleton<Compendium<Trapping>> {
            FirestoreCompendium(Schema.Compendium.TRAPPINGS, instance(), serializer())
        }

        bindSingleton<Compendium<JournalEntry>> {
            FirestoreCompendium(Schema.Compendium.JOURNAL, instance(), serializer())
        }

        bindSingleton { DismissedUserTipsHolder(instance()) }

        bindSingleton<InvitationProcessor> { FirestoreInvitationProcessor(instance(), instance()) }
        bindSingleton<SkillRepository> { characterItemRepository(Schema.Character.SKILLS) }
        bindSingleton<TalentRepository> { characterItemRepository(Schema.Character.TALENTS) }
        bindSingleton<SpellRepository> { characterItemRepository(Schema.Character.SPELLS) }
        bindSingleton<BlessingRepository> { characterItemRepository(Schema.Character.BLESSINGS) }
        bindSingleton<MiracleRepository> { characterItemRepository(Schema.Character.MIRACLES) }
        bindSingleton<InventoryItemRepository> { characterItemRepository(Schema.Character.INVENTORY_ITEMS) }
        bindSingleton<TraitRepository> { characterItemRepository(Schema.Character.TRAITS) }

        bindSingleton<CharacterRepository> {
            CharacterRepositoryIdentityMap(10, FirestoreCharacterRepository(instance()))
        }
        bindSingleton<PartyRepository> {
            PartyRepositoryIdentityMap(10, FirestorePartyRepository(instance()))
        }

        bindSingleton<EncounterRepository> { FirestoreEncounterRepository(instance()) }

        bindSingleton<CharacterAvatarChanger> { CloudFunctionCharacterAvatarChanger(instance()) }

        bindFactory { characterId: CharacterId ->
            CharacterTrappingsDetailScreenModel(
                characterId,
                instance(),
                instance(),
                instance(),
                instance(),
                instance(),
            )
        }

        /**
         * ViewModels
         */

        bindFactory { characterId: CharacterId ->
            CharacterCombatScreenModel(characterId, instance(), instance())
        }
        bindFactory { characterId: CharacterId -> CharacteristicsScreenModel(characterId, instance()) }
        bindFactory { characterId: CharacterId ->
            CharacterScreenModel(
                characterId,
                instance(),
                instance(),
                instance(),
                instance(),
                instance(),
            )
        }
        bindFactory { characterId: CharacterId ->
            CharacterDetailScreenModel(
                characterId,
                instance(),
                instance(),
                instance(),
                instance(),
                instance(),
                instance(),
                instance(),
                instance(),
                instance(),
                instance(),
                instance(),
                instance(),
                instance(),
                instance(),
            )
        }
        bindFactory { partyId: PartyId -> CharacterPickerScreenModel(partyId, instance()) }
        bindFactory { partyId: PartyId -> EncountersScreenModel(partyId, instance()) }
        bindFactory { partyId: PartyId -> PartyScreenModel(partyId, instance()) }
        bindFactory { encounterId: EncounterId ->
            EncounterDetailScreenModel(encounterId, instance(), instance(), instance())
        }
        bindFactory { characterId: CharacterId ->
            CharacterSkillDetailScreenModel(characterId, instance(), instance(), instance())
        }
        bindFactory { characterId: CharacterId ->
            AddSkillScreenModel(characterId, instance(), instance(), instance(), instance())
        }
        bindFactory { characterId: CharacterId ->
            AddBasicSkillsScreenModel(characterId, instance(), instance())
        }
        bindFactory { characterId: CharacterId ->
            CharacterSpellDetailScreenModel(characterId, instance(), instance(), instance())
        }
        bindFactory { characterId: CharacterId ->
            AddSpellScreenModel(characterId, instance(), instance(), instance())
        }
        bindFactory { characterId: CharacterId ->
            CharacterTalentDetailScreenModel(
                characterId,
                instance(),
                instance(),
                instance(),
                instance(),
                instance(),
            )
        }

        bindSingleton {
            AvailableCompendiumItemsFactory(instance(), instance())
        }

        bindFactory { characterId: CharacterId ->
            AddTalentScreenModel(
                characterId,
                instance(),
                instance(),
                instance(),
                instance(),
                instance(),
                instance(),
            )
        }
        bindFactory { characterId: CharacterId ->
            CharacterMiracleDetailScreenModel(characterId, instance(), instance(), instance())
        }
        bindFactory { characterId: CharacterId ->
            AddMiracleScreenModel(characterId, instance(), instance(), instance())
        }
        bindFactory { characterId: CharacterId ->
            CharacterBlessingDetailScreenModel(characterId, instance(), instance(), instance())
        }
        bindFactory { characterId: CharacterId ->
            AddBlessingScreenModel(characterId, instance(), instance(), instance())
        }
        bindFactory { characterId: CharacterId ->
            AddTraitScreenModel(
                characterId,
                instance(),
                instance(),
                instance(),
                instance(),
                instance(),
                instance(),
            )
        }
        bindFactory { characterId: CharacterId ->
            AddTrappingScreenModel(
                characterId,
                instance(),
                instance(),
                instance(),
            )
        }

        bindFactory { partyId: PartyId ->
            BlessingCompendiumScreenModel(partyId, instance(), instance(), instance(), instance())
        }
        bindFactory { partyId: PartyId ->
            CareerCompendiumScreenModel(partyId, instance(), instance(), instance())
        }
        bindFactory { partyId: PartyId ->
            MiracleCompendiumScreenModel(partyId, instance(), instance(), instance(), instance())
        }
        bindFactory { partyId: PartyId ->
            SkillCompendiumScreenModel(partyId, instance(), instance(), instance(), instance())
        }
        bindFactory { partyId: PartyId ->
            SpellCompendiumScreenModel(partyId, instance(), instance(), instance(), instance())
        }
        bindFactory { partyId: PartyId ->
            TalentCompendiumScreenModel(partyId, instance(), instance(), instance(), instance(), instance())
        }
        bindFactory { partyId: PartyId ->
            TraitCompendiumScreenModel(partyId, instance(), instance(), instance(), instance(), instance())
        }
        bindFactory { partyId: PartyId ->
            TrappingCompendiumScreenModel(partyId, instance(), instance(), instance(), instance())
        }
        bindFactory { partyId: PartyId ->
            JournalScreenModel(partyId, instance(), instance(), instance(), instance())
        }

        bindSingleton { EffectManager(instance(), instance(), instance(), instance()) }
        bindSingleton { TrappingSaver(instance(), instance()) }
        bindFactory { characterId: CharacterId ->
            CharacterTraitDetailScreenModel(
                characterId,
                instance(),
                instance(),
                instance(),
                instance(),
                instance(),
            )
        }
        bindProvider { InvitationScreenModel(instance(), instance(), instance()) }
        bindProvider { PartyListScreenModel(instance()) }
        bindProvider { SettingsScreenModel(instance(), instance()) }
        bindFactory { partyId: PartyId ->
            CharacterCreationScreenModel(
                partyId,
                instance(),
                instance(),
                instance(),
                instance(),
            )
        }
        bindFactory { partyId: PartyId ->
            CompendiumExportScreenModel(
                partyId,
                instance(),
                instance(),
                instance(),
                instance(),
                instance(),
                instance(),
                instance(),
                instance(),
                instance(),
            )
        }
        bindFactory { partyId: PartyId -> GameMasterScreenModel(partyId, instance(), instance()) }
        bindFactory { partyId: PartyId ->
            NpcsScreenModel(
                partyId,
                instance(),
                instance(),
            )
        }
        bindFactory { partyId: PartyId ->
            SkillTestScreenModel(partyId, instance(), instance(), instance())
        }
        bindFactory { partyId: PartyId ->
            CombatScreenModel(
                partyId,
                Random,
                instance(),
                instance(),
                instance(),
                instance(),
                instance(),
                instance(),
                instance(),
                instance(),
                instance(),
            )
        }
        bindFactory { partyId: PartyId ->
            PartySettingsScreenModel(partyId, instance(), instance(), instance(), instance())
        }

        bindProvider {

            ChangelogScreenModel(
                HttpClient(CIO) {
                    install(HttpCache)
                    install(ContentNegotiation) {
                        json(
                            Json {
                                ignoreUnknownKeys = true
                                encodeDefaults = true
                            },
                        )
                    }
                },
            )
        }

        bindSingleton {
            Firebase.firestore.apply {
                @Suppress("KotlinConstantConditions")
                if (BuildKonfig.firestoreEmulatorUrl != "") {
                    val (host, port) = BuildKonfig.firestoreEmulatorUrl.split(':')
                    useEmulator(host, port.toInt())
                }
            }
        }

        bindSingleton {
            Firebase.functions.apply {
                @Suppress("KotlinConstantConditions")
                if (BuildKonfig.functionsEmulatorUrl != "") {
                    val (host, port) = BuildKonfig.functionsEmulatorUrl.split(':')
                    useEmulator(host, port.toInt())
                }
            }
        }
    }

private inline fun <reified T : CharacterItem<T, C>, C : CompendiumItem<C>> DirectDI.characterItemRepository(
    collectionName: String,
): CharacterItemRepository<T> {
    return FirestoreCharacterItemRepository(
        collectionName,
        instance(),
        serializer(),
    )
}

internal expect val platformModule: DI.Module
