package cz.frantisekmasa.wfrp_master.core.firestore

import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import cz.frantisekmasa.wfrp_master.core.firestore.jackson.JacksonAggregateMapper
import kotlin.reflect.KClass

fun <T : Any> aggregateMapper(classRef: KClass<T>): AggregateMapper<T> =
    JacksonAggregateMapper(classRef, jacksonTypeRef())
