package cz.frantisekmasa.wfrp_master.religion.ui.blessings

import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.viewModel.CharacterItemViewModel
import cz.frantisekmasa.wfrp_master.religion.domain.Blessing
import cz.frantisekmasa.wfrp_master.religion.domain.BlessingRepository
import cz.frantisekmasa.wfrp_master.compendium.domain.Blessing as CompendiumBlessing

internal class BlessingsViewModel(
    characterId: CharacterId,
    repository: BlessingRepository,
    compendium: Compendium<CompendiumBlessing>,
) : CharacterItemViewModel<Blessing, CompendiumBlessing>(characterId, repository, compendium)
