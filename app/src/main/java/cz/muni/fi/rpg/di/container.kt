package cz.muni.fi.rpg.di

import com.revenuecat.purchases.Purchases
import cz.frantisekmasa.wfrp_master.combat.domain.encounter.Encounter
import cz.frantisekmasa.wfrp_master.combat.domain.encounter.EncounterRepository
import cz.frantisekmasa.wfrp_master.combat.domain.encounter.NpcRepository
import cz.frantisekmasa.wfrp_master.combat.infrastructure.FirestoreEncounterRepository
import cz.frantisekmasa.wfrp_master.combat.infrastructure.FirestoreNpcRepository
import cz.frantisekmasa.wfrp_master.combat.ui.CombatViewModel
import cz.frantisekmasa.wfrp_master.compendium.domain.Blessing
import cz.frantisekmasa.wfrp_master.compendium.domain.Miracle
import cz.frantisekmasa.wfrp_master.compendium.infrastructure.FirestoreCompendium
import cz.frantisekmasa.wfrp_master.compendium.ui.CompendiumViewModel
import cz.frantisekmasa.wfrp_master.core.CoreModule
import cz.frantisekmasa.wfrp_master.core.ads.AdManager
import cz.frantisekmasa.wfrp_master.core.ads.AdViewModel
import cz.frantisekmasa.wfrp_master.core.ads.AdmobLocationProvider
import cz.frantisekmasa.wfrp_master.core.ads.LocationProvider
import cz.frantisekmasa.wfrp_master.core.domain.character.CharacterItem
import cz.frantisekmasa.wfrp_master.core.domain.character.CharacterItemRepository
import cz.frantisekmasa.wfrp_master.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.EncounterId
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.core.firestore.aggregateMapper
import cz.frantisekmasa.wfrp_master.core.firestore.repositories.FirestoreCharacterItemRepository
import cz.frantisekmasa.wfrp_master.core.firestore.repositories.FirestoreCharacterRepository
import cz.frantisekmasa.wfrp_master.core.firestore.repositories.FirestorePartyRepository
import cz.frantisekmasa.wfrp_master.core.firestore.serialization.serializationAggregateMapper
import cz.frantisekmasa.wfrp_master.core.viewModel.PartyViewModel
import cz.frantisekmasa.wfrp_master.core.viewModel.PremiumViewModel
import cz.frantisekmasa.wfrp_master.core.viewModel.SettingsViewModel
import cz.frantisekmasa.wfrp_master.inventory.InventoryModule
import cz.frantisekmasa.wfrp_master.religion.COLLECTION_COMPENDIUM_BLESSINGS
import cz.frantisekmasa.wfrp_master.religion.COLLECTION_COMPENDIUM_MIRACLES
import cz.frantisekmasa.wfrp_master.religion.ReligionModule
import cz.muni.fi.rpg.BuildConfig
import cz.muni.fi.rpg.model.cache.CharacterRepositoryIdentityMap
import cz.muni.fi.rpg.model.cache.PartyRepositoryIdentityMap
import cz.muni.fi.rpg.model.domain.CharacterAvatarChanger
import cz.muni.fi.rpg.model.domain.functions.CloudFunctionCharacterAvatarChanger
import cz.muni.fi.rpg.model.domain.invitation.InvitationProcessor
import cz.muni.fi.rpg.model.domain.skills.Skill
import cz.muni.fi.rpg.model.domain.skills.SkillRepository
import cz.muni.fi.rpg.model.domain.spells.Spell
import cz.muni.fi.rpg.model.domain.talents.Talent
import cz.muni.fi.rpg.model.firestore.COLLECTION_COMPENDIUM_SKILLS
import cz.muni.fi.rpg.model.firestore.COLLECTION_COMPENDIUM_SPELLS
import cz.muni.fi.rpg.model.firestore.COLLECTION_COMPENDIUM_TALENTS
import cz.muni.fi.rpg.model.firestore.COLLECTION_SPELLS
import cz.muni.fi.rpg.model.firestore.COLLECTION_TALENTS
import cz.muni.fi.rpg.model.firestore.FirestoreInvitationProcessor
import cz.muni.fi.rpg.model.firestore.repositories.FirestoreSkillRepository
import cz.muni.fi.rpg.ui.partySettings.PartySettingsViewModel
import cz.muni.fi.rpg.viewModels.AuthenticationViewModel
import cz.muni.fi.rpg.viewModels.CharacterCreationViewModel
import cz.muni.fi.rpg.viewModels.CharacterStatsViewModel
import cz.muni.fi.rpg.viewModels.CharacterViewModel
import cz.muni.fi.rpg.viewModels.EncounterDetailViewModel
import cz.muni.fi.rpg.viewModels.EncountersViewModel
import cz.muni.fi.rpg.viewModels.GameMasterViewModel
import cz.muni.fi.rpg.viewModels.JoinPartyViewModel
import cz.muni.fi.rpg.viewModels.NetworkViewModel
import cz.muni.fi.rpg.viewModels.PartyListViewModel
import cz.muni.fi.rpg.viewModels.SkillTestViewModel
import cz.muni.fi.rpg.viewModels.SkillsViewModel
import cz.muni.fi.rpg.viewModels.SpellsViewModel
import cz.muni.fi.rpg.viewModels.TalentsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.module
import kotlin.random.Random
import kotlin.reflect.KClass
import cz.frantisekmasa.wfrp_master.compendium.domain.Skill as CompendiumSkill
import cz.frantisekmasa.wfrp_master.compendium.domain.Spell as CompendiumSpell
import cz.frantisekmasa.wfrp_master.compendium.domain.Talent as CompendiumTalent

private enum class Services {
    CHARACTER_TALENT_REPOSITORY,
    CHARACTER_SPELL_REPOSITORY,
}

val appModule =
    CoreModule +
        InventoryModule +
        ReligionModule +
        module {
            fun Scope.skillCompendium() = FirestoreCompendium<CompendiumSkill>(
                COLLECTION_COMPENDIUM_SKILLS,
                get(),
                serializationAggregateMapper(),
            )

            fun Scope.talentCompendium() = FirestoreCompendium<CompendiumTalent>(
                COLLECTION_COMPENDIUM_TALENTS,
                get(),
                serializationAggregateMapper(),
            )

            fun Scope.spellCompendium() = FirestoreCompendium<CompendiumSpell>(
                COLLECTION_COMPENDIUM_SPELLS,
                get(),
                serializationAggregateMapper(),
            )

            fun Scope.blessingCompendium() = FirestoreCompendium<Blessing>(
                COLLECTION_COMPENDIUM_BLESSINGS,
                get(),
                serializationAggregateMapper()
            )

            fun Scope.miracleCompendium() = FirestoreCompendium<Miracle>(
                COLLECTION_COMPENDIUM_MIRACLES,
                get(),
                serializationAggregateMapper()
            )

            fun <T : CharacterItem> Scope.characterItemRepository(
                classRef: KClass<T>,
                collectionName: String,
            ): CharacterItemRepository<T> = FirestoreCharacterItemRepository(
                collectionName = collectionName,
                mapper = aggregateMapper(classRef),
                get(),
            )

            single<InvitationProcessor> { FirestoreInvitationProcessor(get(), get()) }

            single<LocationProvider> { AdmobLocationProvider() }
            single {
                Purchases.configure(get(), "TGwuwqSQDUkhhYUtPGLdWilEzpOosKVU").apply {
                    Purchases.debugLogsEnabled = BuildConfig.DEBUG
                }
            }

            single<SkillRepository> { FirestoreSkillRepository(get(), aggregateMapper(Skill::class)) }
            single(named(Services.CHARACTER_TALENT_REPOSITORY)) {
                characterItemRepository(Talent::class, COLLECTION_TALENTS)
            }
            single(named(Services.CHARACTER_SPELL_REPOSITORY)) {
                characterItemRepository(Spell::class, COLLECTION_SPELLS)
            }

            single<CharacterRepository> {
                CharacterRepositoryIdentityMap(10, FirestoreCharacterRepository(get(), serializationAggregateMapper()))
            }
            single<PartyRepository> {
                PartyRepositoryIdentityMap(10, FirestorePartyRepository(get(), serializationAggregateMapper()))
            }

            single<EncounterRepository> { FirestoreEncounterRepository(get(), aggregateMapper(Encounter::class)) }
            single<NpcRepository> { FirestoreNpcRepository(get(), serializationAggregateMapper()) }

            single { AdManager(get()) }

            single<CharacterAvatarChanger> { CloudFunctionCharacterAvatarChanger(get()) }

            /**
             * ViewModels
             */
            viewModel { (characterId: CharacterId) -> CharacterStatsViewModel(characterId, get()) }
            viewModel { (characterId: CharacterId) -> CharacterViewModel(characterId, get()) }
            viewModel { (partyId: PartyId) -> EncountersViewModel(partyId, get()) }
            viewModel { (partyId: PartyId) -> PartyViewModel(partyId, get()) }
            viewModel { (encounterId: EncounterId) -> EncounterDetailViewModel(encounterId, get(), get(), get()) }
            viewModel { (characterId: CharacterId) -> SkillsViewModel(characterId, get(), skillCompendium()) }
            viewModel { (characterId: CharacterId) ->
                SpellsViewModel(
                    characterId,
                    get(named(Services.CHARACTER_SPELL_REPOSITORY)),
                    spellCompendium(),
                )
            }
            viewModel { (characterId: CharacterId) ->
                TalentsViewModel(
                    characterId,
                    get(named(Services.CHARACTER_TALENT_REPOSITORY)),
                    talentCompendium(),
                )
            }
            viewModel { AuthenticationViewModel(get()) }
            viewModel { NetworkViewModel(get()) }
            viewModel { JoinPartyViewModel(get(), get(), get()) }
            viewModel { PartyListViewModel(get()) }
            viewModel { SettingsViewModel(get(), get(), get(), get()) }
            viewModel { PremiumViewModel(get()) }
            viewModel { AdViewModel(get()) }
            viewModel { (partyId: PartyId) -> CharacterCreationViewModel(partyId, get()) }
            viewModel { (partyId: PartyId) ->
                CompendiumViewModel(
                    partyId,
                    skillCompendium(),
                    talentCompendium(),
                    spellCompendium(),
                    blessingCompendium(),
                    miracleCompendium(),
                    get(),
                )
            }
            viewModel { (partyId: PartyId) -> GameMasterViewModel(partyId, get(), get()) }
            viewModel { (partyId: PartyId) -> SkillTestViewModel(partyId, skillCompendium(), get(), get()) }
            viewModel { (partyId: PartyId) -> CombatViewModel(partyId, Random, get(), get(), get()) }
            viewModel { (partyId: PartyId) -> PartySettingsViewModel(partyId, get()) }
        }
