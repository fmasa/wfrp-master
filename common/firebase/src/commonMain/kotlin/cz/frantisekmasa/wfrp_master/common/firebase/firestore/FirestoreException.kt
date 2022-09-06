package cz.frantisekmasa.wfrp_master.common.firebase.firestore

class FirestoreException(
    cause: Throwable,
    val isUnavailable: Boolean,
) : Exception(cause)
