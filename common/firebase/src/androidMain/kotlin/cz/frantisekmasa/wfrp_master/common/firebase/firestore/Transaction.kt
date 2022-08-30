package cz.frantisekmasa.wfrp_master.common.firebase.firestore

import com.google.firebase.firestore.Transaction as NativeTransaction

actual class Transaction(private val transaction: NativeTransaction) {
    actual fun set(documentReference: DocumentReference, fields: Map<String, Any?>, options: SetOptions) {
        transaction.set(documentReference.toNative(), fields, options.toNative())
    }

    actual fun delete(documentReference: DocumentReference) {
        transaction.delete(documentReference.toNative())
    }
}