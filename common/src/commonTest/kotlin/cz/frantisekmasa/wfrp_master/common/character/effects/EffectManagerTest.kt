package cz.frantisekmasa.wfrp_master.common.character.effects

import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.auth.UserId
import cz.frantisekmasa.wfrp_master.common.core.domain.Size
import cz.frantisekmasa.wfrp_master.common.core.domain.Stats
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Points
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Race
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.domain.talents.Talent
import cz.frantisekmasa.wfrp_master.common.core.domain.traits.Trait
import cz.frantisekmasa.wfrp_master.common.dummies.DummyCharacterItemRepository
import cz.frantisekmasa.wfrp_master.common.dummies.DummyCharacterRepository
import cz.frantisekmasa.wfrp_master.common.settings.Language
import dev.icerock.moko.resources.StringResource
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals

class EffectManagerTest {

    @Test
    fun `saveItem() applies the effect of a new item`() {
        val characterId = CharacterId(party.id, character.id)
        val talentRepository = DummyCharacterItemRepository<Talent>()
        val characterRepository = DummyCharacterRepository().apply {
            runBlocking {
                save(party.id, character)
            }
        }

        val effectManager = EffectManager(
            characters = characterRepository,
            traits = DummyCharacterItemRepository(),
            talents = talentRepository,
            translatorFactory = translatorFactory(
                mapOf(
                    Language.EN to mapOf(Str.character_effect_hardy to "hardy")
                )
            ),
        )

        val talent = Talent(
            id = uuid4(),
            compendiumId = null,
            name = "Hardy",
            tests = "",
            description = "",
            taken = 2,
        )

        runBlocking {
            effectManager.saveItem(
                mockk(),
                party,
                characterId,
                talentRepository,
                item = talent,
                previousItemVersion = null,
            )
        }

        assertEquals(
            listOf(talent),
            runBlocking { talentRepository.findAllForCharacter(characterId).first() },
        )
        assertEquals(
            character.copy(
                woundsModifiers = character.woundsModifiers.copy(extraToughnessBonusMultiplier = 2)
            ),
            runBlocking { characterRepository.get(characterId) },
        )
    }

    @Test
    fun `saveItem() applies the updated effect of a new item`() {
        val characterId = CharacterId(party.id, character.id)
        val talentRepository = DummyCharacterItemRepository<Talent>()
        val characterRepository = DummyCharacterRepository().apply {
            runBlocking {
                save(party.id, character)
            }
        }

        val effectManager = EffectManager(
            characters = characterRepository,
            traits = DummyCharacterItemRepository(),
            talents = talentRepository,
            translatorFactory = translatorFactory(
                mapOf(
                    Language.EN to mapOf(Str.character_effect_hardy to "hardy")
                )
            ),
        )

        val talent = Talent(
            id = uuid4(),
            compendiumId = null,
            name = "Hardy",
            tests = "",
            description = "",
            taken = 2,
        )

        runBlocking {
            effectManager.saveItem(
                mockk(),
                party,
                characterId,
                talentRepository,
                item = talent,
                previousItemVersion = null,
            )

            effectManager.saveItem(
                mockk(),
                party,
                characterId,
                talentRepository,
                item = talent.copy(taken = 1),
                previousItemVersion = talent,
            )
        }

        assertEquals(
            character.copy(
                woundsModifiers = character.woundsModifiers.copy(extraToughnessBonusMultiplier = 1)
            ),
            runBlocking { characterRepository.get(characterId) },
        )
    }

    @Test
    fun `saveItem() for not-changed item does nothing`() {
        val characterId = CharacterId(party.id, character.id)
        val talentRepository = DummyCharacterItemRepository<Talent>()
        val characterRepository = DummyCharacterRepository().apply {
            runBlocking {
                save(party.id, character)
            }
        }

        val effectManager = EffectManager(
            characters = characterRepository,
            traits = DummyCharacterItemRepository(),
            talents = talentRepository,
            translatorFactory = translatorFactory(
                mapOf(
                    Language.EN to mapOf(Str.character_effect_savvy to "savvy"),
                )
            ),
        )

        val talent = Talent(
            id = uuid4(),
            compendiumId = null,
            name = "Savvy",
            tests = "",
            description = "",
            taken = 2,
        )

        runBlocking {
            effectManager.saveItem(
                mockk(),
                party,
                characterId,
                talentRepository,
                item = talent,
                previousItemVersion = null,
            )

            effectManager.saveItem(
                mockk(),
                party,
                characterId,
                talentRepository,
                item = talent,
                previousItemVersion = talent,
            )
        }

        assertEquals(
            character.copy(
                characteristicsBase = Stats.ZERO.copy(intelligence = 5),
            ),
            runBlocking { characterRepository.get(characterId) },
        )
    }

    @Test
    fun `saveItem() takes other active effects into account`() {
        val characterId = CharacterId(party.id, character.id)
        val traitRepository = DummyCharacterItemRepository<Trait>()
        val characterRepository = DummyCharacterRepository().apply {
            runBlocking {
                save(party.id, character)
            }
        }

        val effectManager = EffectManager(
            characters = characterRepository,
            traits = traitRepository,
            talents = DummyCharacterItemRepository(),
            translatorFactory = translatorFactory(
                mapOf(
                    Language.EN to mapOf(
                        Str.character_effect_size to "size",
                        Str.character_size_large to "large",
                        Str.character_size_small to "small",
                    ),
                )
            ),
        )

        runBlocking {
            effectManager.saveItem(
                mockk(),
                party,
                characterId,
                traitRepository,
                item = Trait(
                    id = uuid4(),
                    name = "Size (Various)",
                    compendiumId = uuid4(),
                    description = "",
                    specificationValues = mapOf("Various" to "Large"),
                ),
                previousItemVersion = null,
            )

            effectManager.saveItem(
                mockk(),
                party,
                characterId,
                traitRepository,
                item = Trait(
                    id = uuid4(),
                    name = "Size (Various)",
                    compendiumId = uuid4(),
                    description = "",
                    specificationValues = mapOf("Various" to "Small"),
                ),
                previousItemVersion = null,
            )
        }

        assertEquals(
            character.copy(size = Size.LARGE),
            runBlocking { characterRepository.get(characterId) },
        )
    }

    @Test
    fun `reapplyWithDifferentLanguage() removes effects from previous language and adds ones from a new one`() {
        val characterId = CharacterId(party.id, character.id)
        val talentRepository = DummyCharacterItemRepository<Talent>()
        val characterRepository = DummyCharacterRepository().apply {
            runBlocking {
                save(party.id, character)
            }
        }

        val effectManager = EffectManager(
            characters = characterRepository,
            traits = DummyCharacterItemRepository(),
            talents = talentRepository,
            translatorFactory = translatorFactory(
                mapOf(
                    Language.EN to mapOf(
                        Str.character_effect_hardy to "hardy",
                        Str.character_effect_savvy to "savvy"
                    ),
                    Language.IT to mapOf(
                        Str.character_effect_hardy to "something-else",
                        Str.character_effect_savvy to "savvy (it)"
                    )
                )
            ),
        )

        runBlocking {
            effectManager.saveItem(
                mockk(),
                party.copy(settings = party.settings.copy(language = Language.IT)),
                characterId,
                talentRepository,
                item = Talent(
                    id = uuid4(),
                    compendiumId = null,
                    name = "Hardy",
                    tests = "",
                    description = "",
                    taken = 2,
                ),
                previousItemVersion = null,
            )

            effectManager.saveItem(
                mockk(),
                party.copy(settings = party.settings.copy(language = Language.IT)),
                characterId,
                talentRepository,
                item = Talent(
                    id = uuid4(),
                    compendiumId = null,
                    name = "Savvy (IT)",
                    tests = "",
                    description = "",
                    taken = 2,
                ),
                previousItemVersion = null,
            )

            assertEquals(
                character.copy(
                    characteristicsBase = Stats.ZERO.copy(intelligence = 5),
                ),
                runBlocking { characterRepository.get(characterId) },
            )

            effectManager.reapplyWithDifferentLanguage(
                mockk(),
                party.id,
                characterRepository.get(characterId),
                originalLanguage = Language.IT,
                newLanguage = Language.EN,
            )
        }

        assertEquals(
            character.copy(
                woundsModifiers = character.woundsModifiers.copy(extraToughnessBonusMultiplier = 2)
            ),
            runBlocking { characterRepository.get(characterId) },
        )
    }

    @Test
    fun `removeItem() reverts the effect and takes other effects into account`() {
        val characterId = CharacterId(party.id, character.id)
        val traitRepository = DummyCharacterItemRepository<Trait>()
        val characterRepository = DummyCharacterRepository().apply {
            runBlocking {
                save(party.id, character)
            }
        }

        val effectManager = EffectManager(
            characters = characterRepository,
            traits = traitRepository,
            talents = DummyCharacterItemRepository(),
            translatorFactory = translatorFactory(
                mapOf(
                    Language.EN to mapOf(
                        Str.character_effect_size to "size",
                        Str.character_size_large to "large",
                        Str.character_size_small to "small",
                    ),
                )
            ),
        )

        val trait = Trait(
            id = uuid4(),
            name = "Size (Various)",
            compendiumId = uuid4(),
            description = "",
            specificationValues = mapOf("Various" to "Large"),
        )
        runBlocking {
            effectManager.saveItem(
                mockk(),
                party,
                characterId,
                traitRepository,
                item = trait,
                previousItemVersion = null,
            )

            effectManager.saveItem(
                mockk(),
                party,
                characterId,
                traitRepository,
                item = Trait(
                    id = uuid4(),
                    name = "Size (Various)",
                    compendiumId = uuid4(),
                    description = "",
                    specificationValues = mapOf("Various" to "Small"),
                ),
                previousItemVersion = null,
            )

            effectManager.removeItem(
                mockk(),
                party,
                characterId,
                traitRepository,
                trait,
            )
        }

        assertEquals(
            character.copy(size = Size.SMALL),
            runBlocking { characterRepository.get(characterId) },
        )
    }

    private fun translatorFactory(
        translations: Map<Language, Map<StringResource, String>>,
    ): Translator.Factory {
        return mockk {
            for (language in translations.keys) {
                every { create(language) } returns object : Translator {
                    override val locale = Language.EN.locale

                    override fun translate(name: StringResource): String {
                        return translations.getValue(language)[name] ?: "-"
                    }
                }
            }
        }
    }

    companion object {
        private val party = Party(
            id = PartyId.generate(),
            name = "Party",
            gameMasterId = UserId("foo"),
            users = setOf(UserId("foo")),
        )
        private val character = Character(
            id = uuid4().toString(),
            name = "Sigmar",
            career = "",
            characteristicsBase = Stats.ZERO,
            characteristicsAdvances = Stats.ZERO,
            points = Points(
                corruption = 0,
                fate = 0,
                fortune = 0,
                maxWounds = null,
                wounds = 0,
                resilience = 0,
                resolve = 0,
                sin = 0,
                experience = 0,
                spentExperience = 0,
                hardyWoundsBonus = 0,
            ),
            motivation = "",
            psychology = "",
            race = Race.HUMAN,
            socialClass = "",
            userId = null,
        )
    }
}
