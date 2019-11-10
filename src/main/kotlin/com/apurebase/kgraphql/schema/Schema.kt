package com.apurebase.kgraphql.schema

import com.apurebase.kgraphql.Context
import com.apurebase.kgraphql.schema.introspection.__Schema
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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

    /**
     * Executes the given request and returns the first response (default for Query and Mutation).
     * The execution will block until a result is available
     */
    fun executeBlockingGetOne(
        request: String,
        variables: String? = null,
        context: Context = Context(emptyMap())
    ) = runBlocking { execute(request, variables, context).first() }
}
