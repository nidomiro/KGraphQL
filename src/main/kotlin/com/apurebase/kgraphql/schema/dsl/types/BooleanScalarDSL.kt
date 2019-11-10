package com.apurebase.kgraphql.schema.dsl.types

import com.apurebase.kgraphql.schema.SchemaException
import com.apurebase.kgraphql.schema.model.ast.ValueNode
import com.apurebase.kgraphql.schema.scalar.BooleanScalarCoercion
import com.apurebase.kgraphql.schema.scalar.ScalarCoercion
import kotlin.reflect.KClass


class BooleanScalarDSL<T : Any>(kClass: KClass<T>) : ScalarDSL<T, Boolean>(kClass) {

    override fun createCoercionFromFunctions(): ScalarCoercion<T, Boolean> {
        return object : BooleanScalarCoercion<T> {

            val serializeImpl = serialize ?: throw SchemaException(PLEASE_SPECIFY_COERCION)

            val deserializeImpl = deserialize ?: throw SchemaException(PLEASE_SPECIFY_COERCION)

            override fun serialize(instance: T): Boolean = serializeImpl(instance)

            override fun deserialize(raw: Boolean, valueNode: ValueNode?): T = deserializeImpl(raw)
        }
    }
}
