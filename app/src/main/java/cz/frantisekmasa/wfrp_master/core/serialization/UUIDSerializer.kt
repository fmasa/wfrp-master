package cz.frantisekmasa.wfrp_master.core.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.UUID

class UUIDSerializer : KSerializer<UUID> {
    private val serializer = String.serializer()

    override val descriptor = serializer.descriptor

    override fun deserialize(decoder: Decoder): UUID =
        UUID.fromString(serializer.deserialize(decoder))

    override fun serialize(encoder: Encoder, value: UUID) =
        serializer.serialize(encoder, value.toString())
}