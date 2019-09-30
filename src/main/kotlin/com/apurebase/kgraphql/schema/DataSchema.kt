package com.apurebase.kgraphql.schema

import com.apurebase.kgraphql.Context
import com.apurebase.kgraphql.schema.introspection.__Schema
import com.apurebase.kgraphql.schema.jol.Lexer
import com.apurebase.kgraphql.schema.structure2.SchemaModel
import com.apurebase.kgraphqlN.KGraphQL

class DataSchema {
//    internal val model: SchemaModel
//): Schema, __Schema by model {



    fun execute(request: String, variables: String? = null, context: Context? = null): String {

        Lexer(request).advance()

        return "Bang"
    }

}


fun main() {
    val query = "{ test { id } }"


    DataSchema().execute(query).let(::println)

}
