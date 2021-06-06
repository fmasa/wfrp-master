package cz.frantisekmasa.wfrp_master.religion.ui.miracles

import cz.frantisekmasa.wfrp_master.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.compendium.domain.Miracle as CompendiumMiracle
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.core.viewModel.CharacterItemViewModel
import cz.frantisekmasa.wfrp_master.religion.domain.Miracle
import cz.frantisekmasa.wfrp_master.religion.domain.MiracleRepository

internal class MiraclesViewModel(
    characterId: CharacterId,
    repository: MiracleRepository,
    compendium: Compendium<CompendiumMiracle>,
) : CharacterItemViewModel<Miracle, CompendiumMiracle>(characterId, repository, compendium)