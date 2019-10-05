package com.apurebase.kgraphql.schema.jol.ast

sealed class ValueNode(override val loc: Location?): ASTNode {
    class VariableNode(val name: NameNode, loc: Location?): ValueNode(loc)

    class IntValueNode(val value: Int, loc: Location?): ValueNode(loc) {
        constructor(value: String, loc: Location?) : this(value.toInt(), loc)
    }

    class FloatValueNode(val value: Float, loc: Location?): ValueNode(loc) {
        constructor(value: String, loc: Location?) : this(value.toFloat(), loc)
    }

    class StringValueNode(val value: String, val block: Boolean?, loc: Location?): ValueNode(loc)

    class BooleanValueNode(val value: Boolean, loc: Location?): ValueNode(loc)

    class NullValueNode(loc: Location?): ValueNode(loc)

    class EnumValueNode(val value: String, loc: Location?): ValueNode(loc)

    class ListValueNode(val values: List<ValueNode>, loc: Location?): ValueNode(loc)

    class ObjectValueNode(val fields: List<ObjectFieldNode>, loc: Location?): ValueNode(loc) {
        class ObjectFieldNode(
            loc: Location?,
            name: NameNode,
            value: ValueNode
        ): ValueNode(loc)
    }
}
