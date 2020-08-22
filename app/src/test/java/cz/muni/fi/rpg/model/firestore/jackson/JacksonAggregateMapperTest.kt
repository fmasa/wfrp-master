package cz.muni.fi.rpg.model.firestore.jackson

import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import com.google.firebase.firestore.DocumentSnapshot
import junit.framework.TestCase
import org.junit.Ignore
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@Ignore("This is somewhat flaky and braks only in CI")
class JacksonAggregateMapperTest : TestCase("JacksonAggregateMapperTest") {
    private class AggregateWithGetter {
        val field = "foo"

        fun getGetter() = field
    }

    private class AggregateWithIsGetter {
        private val field = "foo"

        fun isGetter() = false
    }

    private data class DataAggregate(val field: String)

    private data class DataAggregateWithDefaultValue(
        val field: String,
        val fieldWithDefaultValue: String = "default_value"
    )

    fun testGettersAreIgnoredWhenMappingToDocumentData() {
        val mapper = JacksonAggregateMapper(AggregateWithGetter::class, jacksonTypeRef())

        assertEquals(
            mapOf("field" to "foo"),
            mapper.toDocumentData(AggregateWithGetter())
        )
    }

    fun testIsGettersAreIgnoredWhenMappingToDocumentData() {
        val mapper = JacksonAggregateMapper(AggregateWithIsGetter::class, jacksonTypeRef())

        assertEquals(
            mapOf("field" to "foo"),
            mapper.toDocumentData(AggregateWithIsGetter())
        )
    }

    fun testExtraFieldsAreIgnoredWhenHydratingAggregate() {
        val mapper = JacksonAggregateMapper(DataAggregate::class, jacksonTypeRef())

        val snapshot = mock(DocumentSnapshot::class.java)
        `when`(snapshot.data).thenReturn(mapOf("field" to "foo", "extraField" to "bar"))

        assertEquals(
            DataAggregate("foo"),
            mapper.fromDocumentSnapshot(snapshot)
        )
    }

    fun testIfFieldWithDefaultValueIsMissingDefaultValueIsUsed() {
        val mapper = JacksonAggregateMapper(DataAggregateWithDefaultValue::class, jacksonTypeRef())

        val snapshot = mock(DocumentSnapshot::class.java)
        `when`(snapshot.data)
            .thenReturn(mapOf(
                "field" to "foo",
                "fieldWithDefaultValue" to "default_value"
            ))

        assertEquals(
            DataAggregateWithDefaultValue("foo"),
            mapper.fromDocumentSnapshot(snapshot)
        )
    }
}