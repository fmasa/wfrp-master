package cz.frantisekmasa.wfrp_master.common.character.religion.blessings

import cz.frantisekmasa.wfrp_master.common.core.CharacterItemScreenModel
import cz.frantisekmasa.wfrp_master.common.core.auth.UserProvider
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.religion.Blessing
import cz.frantisekmasa.wfrp_master.common.core.domain.religion.BlessingRepository
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Blessing as CompendiumBlessing

class BlessingsScreenModel(
    characterId: CharacterId,
    repository: BlessingRepository,
    compendium: Compendium<CompendiumBlessing>,
    userProvider: UserProvider,
    partyRepository: PartyRepository,
) : CharacterItemScreenModel<Blessing, CompendiumBlessing>(
    characterId,
    repository,
    compendium,
    userProvider,
    partyRepository,
)
