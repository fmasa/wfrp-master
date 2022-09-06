package cz.frantisekmasa.wfrp_master.common.core.firebase

import cz.frantisekmasa.wfrp_master.common.core.serialization.UuidSerializer
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.DocumentSnapshot
import io.github.aakira.napier.Napier
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.longOrNull
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlinx.serialization.serializer

class SerializationAggregateMapper<T : Any>(
    private val serializer: KSerializer<T>,
) : AggregateMapper<T> {
    override fun fromDocumentSnapshot(snapshot: DocumentSnapshot): T {
        Napier.d("Mapping document $snapshot to entity")

        val data = snapshot.data ?: error("Snapshot data shouldn't be null")

        return fromDocumentData(data)
    }

    override fun fromDocumentData(data: Map<String, Any?>): T {
        return json.decodeFromJsonElement(serializer, mapToJsonElement(data))
    }

    override fun toDocumentData(aggregate: T): Map<String, Any?> {
        @Suppress("UNCHECKED_CAST")
        return unwrapJsonElement(json.encodeToJsonElement(serializer, aggregate)) as Map<String, Any?>
    }

    private fun listToJsonElement(data: List<*>): JsonElement =
        JsonArray(data.map { convertAnyToJsonElement(it) })

    private fun mapToJsonElement(data: Map<*, *>): JsonElement {
        val map: MutableMap<String, JsonElement> = mutableMapOf()
        data.forEach {
            val key = it.key as? String ?: return@forEach
            map[key] = convertAnyToJsonElement(it.value)
        }
        return JsonObject(map)
    }

    private fun convertAnyToJsonElement(value: Any?) = when (value) {
        null -> JsonNull
        is Boolean -> JsonPrimitive(value)
        is Number -> JsonPrimitive(value)
        is String -> JsonPrimitive(value)
        is Map<*, *> -> mapToJsonElement(value)
        is List<*> -> listToJsonElement(value)
        else -> error("Can't serialize unknown type: $value")
    }

    private fun unwrapJsonElement(value: JsonElement): Any? = when (value) {
        JsonNull -> null
        is JsonArray -> value.map { unwrapJsonElement(it) }
        is JsonObject -> value.mapValues { unwrapJsonElement(it.value) }
        is JsonPrimitive ->
            (
                if (value.isString) value.content else
                    value.booleanOrNull
                        ?: value.intOrNull
                        ?: value.floatOrNull
                        ?: value.longOrNull
                        ?: error("Unknown JSON primitive type ${value.content}")
                )
        else -> error("Unknown JSON type $value")
    }

    companion object {
        private val json = Json {
            encodeDefaults = true
            ignoreUnknownKeys = true
            serializersModule = SerializersModule {
                contextual(UuidSerializer())
            }
        }
    }
}

inline fun <reified T : Any> serializationAggregateMapper(): AggregateMapper<T> {
    return SerializationAggregateMapper(serializer())
}
