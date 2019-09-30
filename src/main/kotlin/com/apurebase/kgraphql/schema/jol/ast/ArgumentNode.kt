package com.apurebase.kgraphql.schema.jol.ast

data class ArgumentNode(
    override val loc: Location?,
    val name: NameNode,
    val value: ValueNode
): ASTNode
