package cz.frantisekmasa.wfrp_master.common.core.firebase.firestore

import dev.gitlive.firebase.EncodeSettings
import dev.gitlive.firebase.firestore.DocumentReference
import dev.gitlive.firebase.firestore.Transaction
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.serializer

/**
 * Creates or updates a document with the given data.
 * If the document exists, the top level fields will be merged.
 *
 * This is important. When the version of the app that does not recognize some of the existing fields
 * saves the document, we want to keep the unknown in the document.
 *
 * The reason we merge only top level fields is that Firestore merge option uses deep merge,
 * so e.g. storing data in Map field prevents removal of keys from that map as the original map
 * and the new map are merged.
 */

inline fun <reified T : Any> Transaction.setWithTopLevelFieldsMerge(
    documentRef: DocumentReference,
    data: T,
) {
    setWithTopLevelFieldsMerge(
        documentRef = documentRef,
        data = data,
        strategy = serializer(),
    )
}

private val buildSettings: EncodeSettings.Builder.() -> Unit = {
    encodeDefaults = true
}

fun <T : Any> Transaction.setWithTopLevelFieldsMerge(
    documentRef: DocumentReference,
    data: T,
    strategy: SerializationStrategy<T>,
) {
    set(
        documentRef = documentRef,
        data = data,
        buildSettings = buildSettings,
        mergeFields = getTopLevelFields(strategy, data),
    )
}

suspend inline fun <reified T : Any> DocumentReference.setWithTopLevelFieldsMerge(data: T) {
    setWithTopLevelFieldsMerge(
        data = data,
        strategy = serializer(),
    )
}

suspend fun <T : Any> DocumentReference.setWithTopLevelFieldsMerge(
    data: T,
    strategy: SerializationStrategy<T>,
) {
    set(
        data = data,
        buildSettings = buildSettings,
        strategy = strategy,
        mergeFields = getTopLevelFields(strategy, data),
    )
}

private fun <T> getTopLevelFields(
    strategy: SerializationStrategy<T>,
    data: T,
): Array<String> {
    return Json.encodeToJsonElement(strategy, data)
        .jsonObject
        .keys
        .toTypedArray()
}
