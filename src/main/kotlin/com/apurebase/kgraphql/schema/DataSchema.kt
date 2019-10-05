package com.apurebase.kgraphql.schema

import com.apurebase.kgraphql.Context
import com.apurebase.kgraphql.KGraphQL
import com.apurebase.kgraphql.schema.introspection.__Schema
import com.apurebase.kgraphql.schema.jol.Lexer
import com.apurebase.kgraphql.schema.jol.Parser
import com.apurebase.kgraphql.schema.jol.ast.DefinitionNode
import com.apurebase.kgraphql.schema.jol.ast.DefinitionNode.*

class DataSchema (
    model: Schema
): Schema, __Schema by model {

    override fun execute(request: String, variables: String?, context: Context): String {

        val parsed = Parser(request).parseDocument()

        parsed.definitions.map {
            when (it) {
                is ExecutableDefinitionNode -> println("Hello")
                is TypeSystemDefinitionNode -> println("Not supported yet")
                is TypeSystemExtensionNode -> println("Extensions ain't supported yet")
            }
        }

        return "Bang"
    }
}

data class Tester(val id: Int, val value: String)

fun main() {
    val query = "{ test { id } }"

    val schema = KGraphQL.schema {
        query("test") {
            resolver { -> Tester(1, "Hello World")}
        }
    }

    DataSchema(schema).execute(query).let(::println)

}
