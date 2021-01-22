package cz.muni.fi.rpg.model.ads

interface LocationProvider {
   suspend fun isUserInEeaOrUnknown(): Boolean
}