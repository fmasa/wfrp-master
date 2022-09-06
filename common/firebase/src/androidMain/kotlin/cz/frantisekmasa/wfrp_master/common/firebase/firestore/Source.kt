package cz.frantisekmasa.wfrp_master.common.firebase.firestore

import com.google.firebase.firestore.Source as NativeSource

fun Source.toNative(): NativeSource = when (this) {
    Source.DEFAULT -> NativeSource.DEFAULT
    Source.SERVER -> NativeSource.SERVER
}
