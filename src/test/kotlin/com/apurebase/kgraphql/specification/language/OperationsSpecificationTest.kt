package com.apurebase.kgraphql.specification.language

import com.apurebase.kgraphql.Specification
import com.apurebase.kgraphql.defaultSchema
import com.apurebase.kgraphql.executeEqualQueries
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.Test

data class Actor(var name : String? = "", var age: Int? = 0)
data class Actress(var name : String? = "", var age: Int? = 0)

@Specification("2.3 Operations")
class OperationsSpecificationTest {

    val schema = defaultSchema {

        query("fizz") {
            resolver{ -> "buzz"}.withArgs {  }
        }

        mutation("createActor") {
            resolver { name : String -> Actor(name, 11) }
        }

        subscription("singleValueSubscription") {
            resolver { ->
                flow {
                    emit(Actor("singleValueSubscription", 1))
                }
            }
        }

        subscription("multipleValueSubscriptionWithDelay") {
            resolver { count: Int ->
                flow {
                    (1..count).forEach {
                        delay(200)
                        emit(Actor("multipleValueSubscriptionWithDelay", it))
                    }
                }
            }
        }
    }

    @Test
    fun `unnamed and named queries are equivalent`(){
        executeEqualQueries(schema,
                mapOf("data" to mapOf("fizz" to "buzz")),
                "{fizz}",
                "query {fizz}",
                "query BUZZ {fizz}"
        )
    }

    @Test
    fun `unnamed and named mutations are equivalent`(){
        executeEqualQueries(schema,
                mapOf("data" to mapOf("createActor" to mapOf("name" to "Kurt Russel"))),
                "mutation {createActor(name : \"Kurt Russel\"){name}}",
                "mutation KURT {createActor(name : \"Kurt Russel\"){name}}"
        )
    }

    @Test
    fun `handle singleValue Subscription`() {
        runBlocking {
            val singleResultList = schema.execute("subscription {singleValueSubscription {name, age} }").toList()
            assertThat(singleResultList, Matchers.hasSize(1))
            assertThat(singleResultList[0], Matchers.containsString("Value 1"))
        }
    }

    @Test
    fun `handle multipleValue Subscription`() {
        val count = 5

        runBlocking {
            val multipleResultList =
                schema.execute("subscription {multipleValueSubscriptionWithDelay(count: $count) {name, age} }").toList()
            assertThat(multipleResultList, Matchers.hasSize(count))


        }
    }

    /*
    @Test
    fun `Subscription return type must be the same as the publisher's`(){
        expect<SchemaException>("Subscription return type must be the same as the publisher's"){
            schema.executeGetFirstBlocking("subscription {subscriptionActress(subscription : \"mySubscription\"){age}}")
        }
    }

     */
}

