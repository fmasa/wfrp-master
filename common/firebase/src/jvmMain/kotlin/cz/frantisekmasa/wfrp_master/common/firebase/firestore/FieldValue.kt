package cz.frantisekmasa.wfrp_master.common.firebase.firestore

import com.google.cloud.firestore.FieldValue as NativeFieldValue

actual fun arrayUnion(vararg fields: Any): Any = NativeFieldValue.arrayUnion(*fields)
