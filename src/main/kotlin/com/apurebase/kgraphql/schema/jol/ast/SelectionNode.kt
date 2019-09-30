package com.apurebase.kgraphql.schema.jol.ast

import com.apurebase.kgraphql.schema.jol.ast.TypeNode.Type.NamedTypeNode

sealed class SelectionNode(
    override val loc: Location?
): ASTNode {

    class FieldNode(
        loc: Location?,
        val alias: NameNode?,
        val name: NameNode,
        val arguments: List<ArgumentNode>?,
        val directives: List<DirectiveNode>?,
        val selectionSet: SelectionSetNode?
    ): SelectionNode(loc)

    sealed class FragmentNode(val directives: List<DirectiveNode>?, loc: Location?): SelectionNode(loc) {
        class FragmentSpreadNode(
            loc: Location?,
            val name: NameNode,
            directives: List<DirectiveNode>?
        ): FragmentNode(directives, loc)

        class InlineFragmentNode(
            loc: Location?,
            val typeCondition: NamedTypeNode?,
            directives: List<DirectiveNode>?,
            val selectionSet: SelectionSetNode
        ): FragmentNode(directives, loc)
    }

}
