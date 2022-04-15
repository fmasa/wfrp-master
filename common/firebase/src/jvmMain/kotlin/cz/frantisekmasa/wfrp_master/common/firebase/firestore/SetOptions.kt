package cz.frantisekmasa.wfrp_master.common.firebase.firestore

import com.google.cloud.firestore.SetOptions as NativeSetOptions

fun SetOptions.toNative(): NativeSetOptions = when(this) {
    SetOptions.MERGE -> NativeSetOptions.merge()
}