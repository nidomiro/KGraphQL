package com.apurebase.kgraphql.schema.execution

import com.apurebase.kgraphql.Context
import com.apurebase.kgraphql.request.VariablesJson
import kotlinx.coroutines.flow.Flow


interface RequestExecutor {
    suspend fun execute(plan: ExecutionPlan, variables: VariablesJson, context: Context): Flow<String>
}