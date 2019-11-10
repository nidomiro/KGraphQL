package com.apurebase.kgraphql.schema

import com.apurebase.kgraphql.Context
import com.apurebase.kgraphql.schema.introspection.__Schema
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking

interface Schema : __Schema {

    suspend fun execute(
        request: String,
        variables: String? = null,
        context: Context = Context(emptyMap())
    ): Flow<String>


    fun executeBlocking(
        request: String,
        variables: String? = null,
        context: Context = Context(emptyMap())
    ) = runBlocking { execute(request, variables, context) }
}
