package cz.frantisekmasa.wfrp_master.common.compendium

import cz.frantisekmasa.wfrp_master.common.compendium.domain.CompendiumItem
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.HydratedFormData

interface CompendiumItemFormData<T : CompendiumItem<T>> : HydratedFormData<T>
