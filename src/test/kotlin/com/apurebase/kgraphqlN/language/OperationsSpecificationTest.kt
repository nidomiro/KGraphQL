package com.apurebase.kgraphqlN.language

import com.apurebase.kgraphql.Specification
import com.apurebase.kgraphqlN.KGraphQL
import org.junit.jupiter.api.Test

@Specification("2.3 Operations")
class OperationsSpecificationTest {



    @Test
    fun abc(): Unit {
        val schema = KGraphQL.schema {

        }
        val query = """
            mutation {
              likeStory(storyID: 12345) {
                story {
                  likeCount
                }
              }
            }
        """.trimIndent()

        val result = schema.execute(query)
    }
}
