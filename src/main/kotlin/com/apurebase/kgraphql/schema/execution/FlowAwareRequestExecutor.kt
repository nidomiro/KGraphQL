package com.apurebase.kgraphql.schema.execution

import com.apurebase.kgraphql.Context
import com.apurebase.kgraphql.request.VariablesJson
import com.apurebase.kgraphql.schema.DefaultSchema
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.coroutines.CoroutineContext


class FlowAwareRequestExecutor(val schema: DefaultSchema) : RequestExecutor, CoroutineScope {
    inner class ExecutionContext(
        val requestContext: Context,
        val executionNodeResults: MutableMap<String, Flow<*>> = mutableMapOf()
    )

    override val coroutineContext: CoroutineContext = Job()
    private val argumentsHandler = ArgumentsHandler(schema)

    private val jsonNodeFactory = JsonNodeFactory.instance

    private val dispatcher = schema.configuration.coroutineDispatcher

    private val objectWriter = schema.configuration.objectMapper.writer().let {
        if (schema.configuration.useDefaultPrettyPrinter) {
            it.withDefaultPrettyPrinter()
        } else {
            it
        }
    }

    override suspend fun execute(plan: ExecutionPlan, variables: VariablesJson, context: Context): Flow<String> {

        val executionContext = ExecutionContext(context)

        return flow {
            val executionResult = executeSubscription(plan, executionContext)
            emit(createJsonResponse(executionResult))
        }

    }

    private suspend fun executeSubscription(
        plan: ExecutionPlan,
        executionContext: ExecutionContext
    ): Map<String, JsonNode> {
        return plan.associate { node: Execution.Node ->
            node.aliasOrKey to executeNode(node, executionContext)
        }
    }

    private suspend fun executeNode(node: Execution.Node, executionContext: ExecutionContext): JsonNode {


    }

    private suspend fun createJsonResponse(executionResult: Map<String, JsonNode>): String {
        val jsonRootNode = jsonNodeFactory.objectNode()
        val jsonDataNode = jsonRootNode.putObject("data")

        executionResult.forEach { (key, value) -> jsonDataNode[key] = value }

        return objectWriter.writeValueAsString(jsonRootNode)
    }

}


