package cz.frantisekmasa.wfrp_master.common.core.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

abstract class NullableSerializer <T : Any>(private val serializer: KSerializer<T>) : KSerializer<T?> {
    override val descriptor: SerialDescriptor = serializer.descriptor

    override fun deserialize(decoder: Decoder): T? {
        return if (decoder.decodeNotNullMark()) {
            decoder.decodeSerializableValue(serializer)
        } else {
            decoder.decodeNull()
        }
    }

    override fun serialize(encoder: Encoder, value: T?) {
        if (value == null) {
            encoder.encodeNull()
        } else {
            encoder.encodeSerializableValue(serializer, value)
        }
    }
}
