package cz.frantisekmasa.wfrp_master.common.firebase.firestore

import com.google.cloud.firestore.SetOptions as NativeSetOptions

fun SetOptions.toNative(): NativeSetOptions = when(fieldsMask) {
    null -> NativeSetOptions.merge()
    else -> NativeSetOptions.mergeFields(fieldsMask)
}