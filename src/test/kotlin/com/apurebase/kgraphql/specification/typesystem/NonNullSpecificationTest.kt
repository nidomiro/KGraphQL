package com.apurebase.kgraphql.specification.typesystem

import com.apurebase.kgraphql.*
import com.apurebase.kgraphql.GraphQLError
import org.amshove.kluent.invoking
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldThrow
import org.amshove.kluent.with
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

@Specification("3.1.8 Non-null")
class NonNullSpecificationTest {

    @Test
    fun `if the result of non-null type is null, error should be raised`(){
        val schema = KGraphQL.schema {
            query("nonNull"){
                resolver { string : String? -> string!! }
            }
        }
        expect<NullPointerException> {
            schema.executeBlockingGetOne("{nonNull}")
        }
    }

    @Test
    fun `nullable input types are always optional`(){
        val schema = KGraphQL.schema {
            query("nullable"){
                resolver { input: String? -> input }
            }
        }

        val responseOmittedInput = deserialize(schema.executeBlockingGetOne("{nullable}"))
        assertThat(responseOmittedInput.extract<Any?>("data/nullable"), nullValue())

        val responseNullInput = deserialize(schema.executeBlockingGetOne("{nullable(input: null)}"))
        assertThat(responseNullInput.extract<Any?>("data/nullable"), nullValue())
    }

    @Test
    fun `non-null types are always required`() {
        val schema = KGraphQL.schema {
            query("nonNull"){
                resolver { input: String -> input }
            }
        }
        invoking {
            schema.executeBlockingGetOne("{nonNull}")
        } shouldThrow GraphQLError::class with {
            message shouldEqual "Missing value for non-nullable argument input on the field 'nonNull'"
        }
    }

    @Test
    fun `variable of a nullable type cannot be provided to a non-null argument`(){
        val schema = KGraphQL.schema {
            query("nonNull"){
                resolver { input: String -> input }
            }
        }

        schema.executeBlockingGetOne("query(\$arg: String!){nonNull(input: \$arg)}", "{\"arg\":\"SAD\"}")
    }

}
