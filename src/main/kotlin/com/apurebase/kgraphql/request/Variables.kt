package com.apurebase.kgraphql.request

import com.apurebase.kgraphql.ExecutionException
import com.apurebase.kgraphql.RequestException
import com.apurebase.kgraphql.getIterableElementType
import com.apurebase.kgraphql.isIterable
import com.apurebase.kgraphql.schema.jol.ast.TypeNode
import com.apurebase.kgraphql.schema.jol.ast.ValueNode
import com.apurebase.kgraphql.schema.jol.ast.VariableDefinitionNode
import com.apurebase.kgraphql.schema.structure2.LookupSchema
import kotlin.reflect.KClass
import kotlin.reflect.KType

@Suppress("UNCHECKED_CAST")
data class Variables(
        private val typeDefinitionProvider: LookupSchema,
        private val variablesJson: VariablesJson,
        private val variables: List<VariableDefinitionNode>?
) {

    /**
     * map and return object of requested class
     */
    fun <T : Any> get(kClass: KClass<T>, kType: KType, typeName: String?, keyNode: ValueNode.VariableNode, transform: (value: ValueNode) -> Any?): T? {
        val variable = variables?.find { keyNode.name.value == it.variable.name.value }
                ?: throw IllegalArgumentException("Variable '$${keyNode.name.value}' was not declared for this operation")

        val isIterable = kClass.isIterable()

        validateVariable(typeDefinitionProvider.typeReference(kType), typeName, variable)

        var value = variablesJson.get(kClass, kType, keyNode.name.value)
        if(value == null && variable.defaultValue != null){
            value = transformDefaultValue(transform, variable.defaultValue, kClass)
        }

        value?.let {
            if (isIterable && kType.getIterableElementType()?.isMarkedNullable == false) {
                for (element in value as Iterable<*>) {
                    if (element == null) {
                        throw RequestException(
                                "Invalid argument value $value from variable $${keyNode.name.value}, expected list with non null arguments"
                        )
                    }
                }
            }
        }

        return value
    }

    private fun <T : Any> transformDefaultValue(transform: (value: ValueNode) -> Any?, defaultValue: ValueNode, kClass: KClass<T>): T? {
        val transformedDefaultValue = transform.invoke(defaultValue)
        return when {
            transformedDefaultValue == null -> null
            kClass.isInstance(transformedDefaultValue) -> transformedDefaultValue as T?
            else -> {
                throw ExecutionException("Invalid transform function returned ")
            }
        }
    }

    private fun validateVariable(expectedType: TypeReference, expectedTypeName: String?, variable: VariableDefinitionNode){
        val variableType = variable.type

        val invalidName = (expectedTypeName ?: expectedType.name) != variable.type.nameNode.value
        val invalidIsList = expectedType.isList != variableType.isList
        val invalidNullability = !expectedType.isNullable && variableType !is TypeNode.NonNullTypeNode && variable.defaultValue == null

        // TODO: No clue if this is correct
        val invalidElementNullability = !expectedType.isElementNullable && when (variableType) {
            is TypeNode.ListTypeNode -> variableType.isElementNullable
            else -> false
        }

        if(invalidName || invalidIsList || invalidNullability || invalidElementNullability){
            throw RequestException("Invalid variable $${variable.variable.name.value} argument type ${variableType.nameNode.value}, expected $expectedType")
        }
    }
}
