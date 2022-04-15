package cz.frantisekmasa.wfrp_master.common.encounters.domain

import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.NpcId

class NpcNotFound(id: NpcId, cause: Throwable? =  null) : Exception("Npc $id was not found", cause)