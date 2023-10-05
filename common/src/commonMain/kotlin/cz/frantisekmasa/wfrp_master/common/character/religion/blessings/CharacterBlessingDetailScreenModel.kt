package cz.frantisekmasa.wfrp_master.common.character.religion.blessings

import cz.frantisekmasa.wfrp_master.common.core.CharacterItemScreenModel
import cz.frantisekmasa.wfrp_master.common.core.auth.UserProvider
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.religion.Blessing
import cz.frantisekmasa.wfrp_master.common.core.domain.religion.BlessingRepository

class CharacterBlessingDetailScreenModel(
    characterId: CharacterId,
    repository: BlessingRepository,
    userProvider: UserProvider,
    partyRepository: PartyRepository,
) : CharacterItemScreenModel<Blessing>(
    characterId,
    repository,
    userProvider,
    partyRepository,
)
