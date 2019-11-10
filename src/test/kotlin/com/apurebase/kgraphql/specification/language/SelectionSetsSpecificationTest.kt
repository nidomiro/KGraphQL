package com.apurebase.kgraphql.specification.language

import com.apurebase.kgraphql.*
import com.apurebase.kgraphql.Actor
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

@Specification("2.4 Selection Sets")
class SelectionSetsSpecificationTest {

    val age = 432

    val schema = defaultSchema {
        query("actor") {
            resolver { -> Actor("Boguś Linda", age) }
        }
    }

    @Test
    fun `operation selects the set of information it needs`() {
        val response = deserialize(schema.executeBlockingGetOne("{actor{name, age}}"))
        val map = response.extract<Map<String, Any>>("data/actor")
        assertThat(map, equalTo(mapOf("name" to "Boguś Linda", "age" to age)))
    }

    @Test
    fun `operation selects the set of information it needs 2`() {
        val response = deserialize(schema.executeBlockingGetOne("{actor{name}}"))
        val map = response.extract<Map<String, Any>>("data/actor")
        assertThat(map, equalTo(mapOf<String, Any>("name" to "Boguś Linda")))
    }

    @Test
    fun `operation selects the set of information it needs 3`() {
        val response = deserialize(schema.executeBlockingGetOne("{actor{age}}"))
        val map = response.extract<Map<String, Any>>("data/actor")
        assertThat(map, equalTo(mapOf<String, Any>("age" to age)))
    }

}
