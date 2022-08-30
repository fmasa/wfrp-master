package cz.frantisekmasa.wfrp_master.common

import cz.frantisekmasa.wfrp_master.common.changelog.ChangelogScreenModel
import cz.frantisekmasa.wfrp_master.common.character.CharacterPickerScreenModel
import cz.frantisekmasa.wfrp_master.common.character.CharacterScreenModel
import cz.frantisekmasa.wfrp_master.common.character.characteristics.CharacteristicsScreenModel
import cz.frantisekmasa.wfrp_master.common.character.combat.CharacterCombatScreenModel
import cz.frantisekmasa.wfrp_master.common.character.religion.blessings.BlessingsScreenModel
import cz.frantisekmasa.wfrp_master.common.character.religion.miracles.MiraclesScreenModel
import cz.frantisekmasa.wfrp_master.common.character.skills.SkillsScreenModel
import cz.frantisekmasa.wfrp_master.common.character.spells.SpellsScreenModel
import cz.frantisekmasa.wfrp_master.common.character.talents.TalentsScreenModel
import cz.frantisekmasa.wfrp_master.common.character.traits.TraitsScreenModel
import cz.frantisekmasa.wfrp_master.common.character.trappings.TrappingsScreenModel
import cz.frantisekmasa.wfrp_master.common.characterCreation.CharacterCreationScreenModel
import cz.frantisekmasa.wfrp_master.common.combat.CombatScreenModel
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumScreenModel
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Blessing
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Miracle
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Skill
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Spell
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Talent
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trait
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
import cz.frantisekmasa.wfrp_master.common.core.firebase.repositories.FirestoreNpcRepository
import cz.frantisekmasa.wfrp_master.common.core.firebase.repositories.FirestorePartyRepository
import cz.frantisekmasa.wfrp_master.common.core.firebase.repositories.FirestoreSkillRepository
import cz.frantisekmasa.wfrp_master.common.core.serialization.UuidSerializer
import cz.frantisekmasa.wfrp_master.common.core.tips.DismissedUserTipsHolder
import cz.frantisekmasa.wfrp_master.common.encounters.EncounterDetailScreenModel
import cz.frantisekmasa.wfrp_master.common.encounters.EncountersScreenModel
import cz.frantisekmasa.wfrp_master.common.encounters.domain.EncounterRepository
import cz.frantisekmasa.wfrp_master.common.encounters.domain.NpcRepository
import cz.frantisekmasa.wfrp_master.common.gameMaster.GameMasterScreenModel
import cz.frantisekmasa.wfrp_master.common.invitation.InvitationScreenModel
import cz.frantisekmasa.wfrp_master.common.invitation.domain.FirestoreInvitationProcessor
import cz.frantisekmasa.wfrp_master.common.invitation.domain.InvitationProcessor
import cz.frantisekmasa.wfrp_master.common.npcs.NpcsScreenModel
import cz.frantisekmasa.wfrp_master.common.partyList.PartyListScreenModel
import cz.frantisekmasa.wfrp_master.common.partySettings.PartySettingsScreenModel
import cz.frantisekmasa.wfrp_master.common.settings.SettingsScreenModel
import cz.frantisekmasa.wfrp_master.common.skillTest.SkillTestScreenModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import org.kodein.di.DI
import org.kodein.di.DirectDI
import org.kodein.di.bindFactory
import org.kodein.di.bindProvider
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import kotlin.random.Random
import cz.frantisekmasa.wfrp_master.common.core.firebase.serializationAggregateMapper as mapper


val appModule = DI.Module("Common") {
    import(platformModule)

    bindSingleton {
        Json {
            encodeDefaults = true
            serializersModule = SerializersModule {
                contextual(UuidSerializer())
            }
        }
    }

    bindSingleton<Compendium<Skill>> {
        FirestoreCompendium(Schema.Compendium.Skills, instance(), mapper())
    }

    bindSingleton<Compendium<Talent>> {
        FirestoreCompendium(Schema.Compendium.Talents, instance(), mapper())
    }

    bindSingleton<Compendium<Spell>> {
        FirestoreCompendium(Schema.Compendium.Spells, instance(), mapper())
    }

    bindSingleton<Compendium<Blessing>> {
        FirestoreCompendium(Schema.Compendium.Blessings, instance(), mapper())
    }

    bindSingleton<Compendium<Miracle>> {
        FirestoreCompendium(Schema.Compendium.Miracles, instance(), mapper())
    }

    bindSingleton<Compendium<Trait>> {
        FirestoreCompendium(Schema.Compendium.Traits, instance(), mapper())
    }

    bindSingleton { DismissedUserTipsHolder(instance()) }

    bindSingleton<InvitationProcessor> { FirestoreInvitationProcessor(instance(), instance()) }
    bindSingleton<SkillRepository> { FirestoreSkillRepository(instance(), mapper()) }
    bindSingleton<TalentRepository> { characterItemRepository(Schema.Character.Talents) }
    bindSingleton<SpellRepository> { characterItemRepository(Schema.Character.Spells) }
    bindSingleton<BlessingRepository> { characterItemRepository(Schema.Character.Blessings) }
    bindSingleton<MiracleRepository> { characterItemRepository(Schema.Character.Miracles) }
    bindSingleton<InventoryItemRepository> { characterItemRepository(Schema.Character.InventoryItems) }
    bindSingleton<TraitRepository> { characterItemRepository(Schema.Character.Traits) }

    bindSingleton<CharacterRepository> {
        CharacterRepositoryIdentityMap(10, FirestoreCharacterRepository(instance(), mapper()))
    }
    bindSingleton<PartyRepository> {
        PartyRepositoryIdentityMap(10, FirestorePartyRepository(instance(), mapper()))
    }

    bindSingleton<EncounterRepository> { FirestoreEncounterRepository(instance(), mapper()) }
    bindSingleton<NpcRepository> { FirestoreNpcRepository(instance(), mapper()) }

    bindSingleton<CharacterAvatarChanger> { CloudFunctionCharacterAvatarChanger(instance()) }

    bindFactory { characterId: CharacterId ->
        TrappingsScreenModel(characterId, instance(), instance())
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
            instance()
        )
    }
    bindFactory { partyId: PartyId -> CharacterPickerScreenModel(partyId, instance()) }
    bindFactory { partyId: PartyId -> EncountersScreenModel(partyId, instance()) }
    bindFactory { partyId: PartyId -> PartyScreenModel(partyId, instance()) }
    bindFactory { encounterId: EncounterId ->
        EncounterDetailScreenModel(encounterId, instance(), instance(), instance(), instance())
    }
    bindFactory { characterId: CharacterId ->
        SkillsScreenModel(characterId, instance(), instance())
    }
    bindFactory { characterId: CharacterId ->
        SpellsScreenModel(characterId, instance(), instance())
    }
    bindFactory { characterId: CharacterId ->
        TalentsScreenModel(characterId, instance(), instance())
    }
    bindFactory { characterId: CharacterId ->
        MiraclesScreenModel(characterId, instance(), instance())
    }
    bindFactory { characterId: CharacterId ->
        BlessingsScreenModel(characterId, instance(), instance())
    }

    bindFactory { characterId: CharacterId ->
        TraitsScreenModel(characterId, instance(), instance())
    }
    bindProvider { InvitationScreenModel(instance(), instance(), instance()) }
    bindProvider { PartyListScreenModel(instance()) }
    bindProvider { SettingsScreenModel(instance(), instance()) }
    bindFactory { partyId: PartyId -> CharacterCreationScreenModel(partyId, instance()) }
    bindFactory { partyId: PartyId ->
        CompendiumScreenModel(
            partyId,
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
    bindFactory { partyId: PartyId -> NpcsScreenModel(partyId, instance()) }
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
    bindFactory { partyId: PartyId -> PartySettingsScreenModel(partyId, instance()) }

    bindProvider {
        ChangelogScreenModel(
            HttpClient(ktorEngine) {
                install(HttpCache)
                install(ContentNegotiation) {
                    json(Json {
                        ignoreUnknownKeys = true
                        encodeDefaults = true
                    })
                }
            }
        )
    }
}

private inline fun <reified T : CharacterItem> DirectDI.characterItemRepository(
    collectionName: String
): CharacterItemRepository<T> {
    return FirestoreCharacterItemRepository(
        collectionName,
        mapper(), instance()
    )
}

internal expect val ktorEngine: HttpClientEngine
internal expect val platformModule: DI.Module
