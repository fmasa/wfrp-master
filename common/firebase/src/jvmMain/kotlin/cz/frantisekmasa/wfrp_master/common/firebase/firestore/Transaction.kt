package cz.frantisekmasa.wfrp_master.common.firebase.firestore

import com.google.cloud.firestore.WriteBatch

actual class Transaction(private val batch: WriteBatch) {
    actual fun set(documentReference: DocumentReference, fields: Map<String, Any?>, options: SetOptions) {
        batch.set(documentReference.toNative(), fields, options.toNative())
    }

    actual fun delete(documentReference: DocumentReference) {
        batch.delete(documentReference.toNative())
    }
}
