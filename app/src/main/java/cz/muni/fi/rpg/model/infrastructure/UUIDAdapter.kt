package cz.muni.fi.rpg.model.infrastructure

import com.google.gson.*
import java.lang.IllegalArgumentException
import java.lang.reflect.Type
import java.util.*

class UUIDAdapter : JsonSerializer<UUID>, JsonDeserializer<UUID>
{
    override fun serialize(
        src: UUID?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return context?.serialize(src?.toString()) ?: JsonNull.INSTANCE;
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): UUID {
        if (json == null) {
            throw JsonParseException("Cannot convert NULL to UUID");
        }

        try {
            return UUID.fromString(json.asString);
        } catch (e: IllegalArgumentException) {
            throw JsonParseException(e)
        }
    }
}