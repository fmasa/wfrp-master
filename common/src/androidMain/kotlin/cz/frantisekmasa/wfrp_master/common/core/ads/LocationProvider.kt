package cz.frantisekmasa.wfrp_master.common.core.ads

interface LocationProvider {
    suspend fun isUserInEeaOrUnknown(): Boolean
}
