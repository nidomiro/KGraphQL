package com.apurebase.kgraphql.schema.jol.ast

sealed class TypeNode(override val loc: Location?): ASTNode {
    sealed class Type(loc: Location?): TypeNode(loc) {
        class NamedTypeNode(loc: Location?, val name: NameNode): Type(loc)
        class ListTypeNode(loc: Location?, val type: TypeNode): Type(loc)
    }

    class NonNullTypeNode(loc: Location?, val type: TypeNode): TypeNode(loc)
}
