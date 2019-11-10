package com.apurebase.kgraphql.specification.language

import com.apurebase.kgraphql.Specification
import com.apurebase.kgraphql.defaultSchema
import com.apurebase.kgraphql.executeEqualQueries
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
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
                    emit("Value 1")
                }
            }
        }

        subscription("multipleValueSubscriptionWithDelay") {
            resolver { count: Int ->
                flow {
                    (1..count).forEach {
                        delay(200)
                        emit("Value $it")
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
    fun `handle subscription`(){

        val count = 5

        runBlocking {
            val singleResultList = schema.executeFlow("subscription {singleValueSubscription()}").toList()
            assertThat(singleResultList, Matchers.hasSize(1))
            assertThat(singleResultList[0], Matchers.containsString("Value 1"))

            val multipleResultList =
                schema.executeFlow("subscription {multipleValueSubscriptionWithDelay(count: $count)}").toList()
            assertThat(multipleResultList, Matchers.hasSize(count))


        }


    }

    /*
    @Test
    fun `Subscription return type must be the same as the publisher's`(){
        expect<SchemaException>("Subscription return type must be the same as the publisher's"){
            schema.executeBlocking("subscription {subscriptionActress(subscription : \"mySubscription\"){age}}")
        }
    }

     */
}

