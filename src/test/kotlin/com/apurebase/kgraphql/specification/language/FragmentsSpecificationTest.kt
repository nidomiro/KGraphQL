package com.apurebase.kgraphql.specification.language

import com.apurebase.kgraphql.*
import com.apurebase.kgraphql.Actor
import com.apurebase.kgraphql.GraphQLError
import com.apurebase.kgraphql.integration.BaseSchemaTest
import com.apurebase.kgraphql.integration.BaseSchemaTest.Companion.INTROSPECTION_QUERY
import org.amshove.kluent.invoking
import org.amshove.kluent.shouldThrow
import org.amshove.kluent.withMessage
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

@Specification("2.8 Fragments")
class FragmentsSpecificationTest {

    val age = 232

    val actorName = "Boguś Linda"

    val id = "BLinda"

    data class ActorWrapper(val id: String, val actualActor: Actor)

    val schema = defaultSchema {
        query("actor") {
            resolver { -> ActorWrapper(id, Actor(actorName, age)) }
        }
    }

    val BaseTestSchema = object : BaseSchemaTest() {}

    @Test
    fun `fragment's fields are added to the query at the same level as the fragment invocation`() {
        val expected = mapOf("data" to mapOf(
                "actor" to mapOf(
                        "id" to id,
                        "actualActor" to mapOf("name" to actorName, "age" to age)
                ))
        )
        executeEqualQueries(schema,
                expected,
                "{actor{id, actualActor{name, age}}}",
                "{actor{ ...actWrapper}} fragment actWrapper on ActorWrapper {id, actualActor{ name, age }}"
        )
    }

    @Test
    fun `fragments can be nested`() {
        val expected = mapOf("data" to mapOf(
                "actor" to mapOf(
                        "id" to id,
                        "actualActor" to mapOf("name" to actorName, "age" to age)
                ))
        )
        executeEqualQueries(schema,
                expected,
                "{actor{id, actualActor{name, age}}}",
                "{actor{ ...actWrapper}} fragment act on Actor{name, age} fragment actWrapper on ActorWrapper {id, actualActor{ ...act }}"
        )
    }

    @Test
    fun `Inline fragments may also be used to apply a directive to a group of fields`() {
        val response = deserialize(
            schema.executeBlockingGetOne(
                "query (\$expandedInfo : Boolean!){actor{actualActor{name ... @include(if: \$expandedInfo){ age }}}}",
                "{\"expandedInfo\":false}"
        ))
        assertNoErrors(response)
        assertThat(extractOrNull(response, "data/actor/actualActor/name"), equalTo("Boguś Linda"))
        assertThat(extractOrNull(response, "data/actor/actualActor/age"), nullValue())
    }

    @Test
    fun `query with inline fragment with type condition`() {
        val map = BaseTestSchema.execute("{people{name, age, ... on Actor {isOld} ... on Director {favActors{name}}}}")
        assertNoErrors(map)
        for (i in map.extract<List<*>>("data/people").indices) {
            val name = map.extract<String>("data/people[$i]/name")
            when (name) {
                "David Fincher" /* director */ -> {
                    MatcherAssert.assertThat(map.extract<List<*>>("data/people[$i]/favActors"), CoreMatchers.notNullValue())
                    MatcherAssert.assertThat(extractOrNull<Boolean>(map, "data/people[$i]/isOld"), CoreMatchers.nullValue())
                }
                "Brad Pitt" /* actor */ -> {
                    MatcherAssert.assertThat(map.extract<Boolean>("data/people[$i]/isOld"), CoreMatchers.notNullValue())
                    MatcherAssert.assertThat(extractOrNull<List<*>>(map, "data/people[$i]/favActors"), CoreMatchers.nullValue())
                }
            }
        }
    }

    @Test
    fun `query with external fragment with type condition`() {
        val map = BaseTestSchema.execute("{people{name, age ...act ...dir}} fragment act on Actor {isOld} fragment dir on Director {favActors{name}}")
        assertNoErrors(map)
        for (i in map.extract<List<*>>("data/people").indices) {
            val name = map.extract<String>("data/people[$i]/name")
            when (name) {
                "David Fincher" /* director */ -> {
                    MatcherAssert.assertThat(map.extract<List<*>>("data/people[$i]/favActors"), CoreMatchers.notNullValue())
                    MatcherAssert.assertThat(extractOrNull<Boolean>(map, "data/people[$i]/isOld"), CoreMatchers.nullValue())
                }
                "Brad Pitt" /* actor */ -> {
                    MatcherAssert.assertThat(map.extract<Boolean>("data/people[$i]/isOld"), CoreMatchers.notNullValue())
                    MatcherAssert.assertThat(extractOrNull<List<*>>(map, "data/people[$i]/favActors"), CoreMatchers.nullValue())
                }
            }
        }
    }

    @Test
    fun `multiple nested fragments are handled`() {
        val map = BaseTestSchema.execute(INTROSPECTION_QUERY)
        val fields = map.extract<List<Map<String,*>>>("data/__schema/types[0]/fields")

        fields.forEach { field ->
            assertThat(field["name"], notNullValue())
        }
    }

    @Test
    fun `queries with recursive fragments are denied`() {
        invoking {
            BaseTestSchema.execute("""
            query IntrospectionQuery {
                __schema {
                    types {
                        ...FullType
                    }
                }
            }

            fragment FullType on __Type {
                fields(includeDeprecated: true) {
                    name
                    type {
                        ...FullType
                    }
                }
            }
        """)
        } shouldThrow GraphQLError::class withMessage "Fragment spread circular references are not allowed"
    }
}
