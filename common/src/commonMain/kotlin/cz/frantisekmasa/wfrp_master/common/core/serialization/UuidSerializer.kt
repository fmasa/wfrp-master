package cz.frantisekmasa.wfrp_master.common.core.serialization

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuidFrom
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class UuidSerializer : KSerializer<Uuid> {
    private val serializer = String.serializer()

    override val descriptor = serializer.descriptor

    override fun deserialize(decoder: Decoder): Uuid = uuidFrom(serializer.deserialize(decoder))

    override fun serialize(
        encoder: Encoder,
        value: Uuid,
    ) = serializer.serialize(encoder, value.toString())
}

typealias UuidAsString =
    @Serializable(UuidSerializer::class)
    Uuid
