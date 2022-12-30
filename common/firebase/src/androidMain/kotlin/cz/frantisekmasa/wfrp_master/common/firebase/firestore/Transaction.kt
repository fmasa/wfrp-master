package cz.frantisekmasa.wfrp_master.common.firebase.firestore

import com.google.firebase.firestore.WriteBatch as NativeBatch

actual class Transaction(private val batch: NativeBatch) {
    actual fun set(documentReference: DocumentReference, fields: Map<String, Any?>, options: SetOptions) {
        batch.set(documentReference.toNative(), fields, options.toNative())
    }

    actual fun delete(documentReference: DocumentReference) {
        batch.delete(documentReference.toNative())
    }
}
