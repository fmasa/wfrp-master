package cz.frantisekmasa.wfrp_master.common.character.religion.miracles

import cz.frantisekmasa.wfrp_master.common.core.CharacterItemScreenModel
import cz.frantisekmasa.wfrp_master.common.core.auth.UserProvider
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.religion.Miracle
import cz.frantisekmasa.wfrp_master.common.core.domain.religion.MiracleRepository

class CharacterMiracleDetailScreenModel(
    characterId: CharacterId,
    repository: MiracleRepository,
    userProvider: UserProvider,
    partyRepository: PartyRepository,
) : CharacterItemScreenModel<Miracle>(
        characterId,
        repository,
        userProvider,
        partyRepository,
    )
