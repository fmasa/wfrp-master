package cz.frantisekmasa.wfrp_master.core.ads

interface LocationProvider {
    suspend fun isUserInEeaOrUnknown(): Boolean
}
