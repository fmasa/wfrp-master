package cz.muni.fi.rpg.model.firestore.jackson

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.firebase.firestore.DocumentSnapshot
import cz.muni.fi.rpg.model.firestore.AggregateMapper
import cz.muni.fi.rpg.model.firestore.DocumentData
import timber.log.Timber
import kotlin.reflect.KClass

internal class JacksonAggregateMapper<T : Any>(
    private val aggregateType: KClass<T>,
    private val documentDataType: TypeReference<DocumentData>
) : AggregateMapper<T> {

    private val mapper = jacksonObjectMapper()
        .setVisibility(PropertyAccessor.GETTER, Visibility.NONE)
        .setVisibility(PropertyAccessor.FIELD, Visibility.ANY)
        .setVisibility(PropertyAccessor.IS_GETTER, Visibility.NONE)

    override fun fromDocumentSnapshot(snapshot: DocumentSnapshot): T {
        Timber.d("Mapping document $snapshot to ${aggregateType.simpleName}")
        return mapper.convertValue(snapshot.data, aggregateType.java)
    }

    override fun toDocumentData(aggregate: T): DocumentData {
        return mapper.convertValue(aggregate, documentDataType)
    }
}