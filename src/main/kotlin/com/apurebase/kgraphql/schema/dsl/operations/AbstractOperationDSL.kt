package com.apurebase.kgraphql.schema.dsl.operations

import com.apurebase.kgraphql.Context
import com.apurebase.kgraphql.schema.dsl.LimitedAccessItemDSL
import com.apurebase.kgraphql.schema.dsl.ResolverDSL
import com.apurebase.kgraphql.schema.model.FunctionWrapper
import com.apurebase.kgraphql.schema.model.InputValueDef


abstract class AbstractOperationDSL(
    val name: String
) : LimitedAccessItemDSL<Nothing>(),
    ResolverDSL.Target {

    protected val inputValues = mutableListOf<InputValueDef<*>>()

    internal var functionWrapper : FunctionWrapper<*>? = null

    protected fun resolver(function: FunctionWrapper<*>): ResolverDSL {
        functionWrapper = function
        return ResolverDSL(this)
    }

    fun accessRule(rule: (Context) -> Exception?){
        val accessRuleAdapter: (Nothing?, Context) -> Exception? = { _, ctx -> rule(ctx) }
        this.accessRuleBlock = accessRuleAdapter
    }

    override fun addInputValues(inputValues: Collection<InputValueDef<*>>) {
        this.inputValues.addAll(inputValues)
    }


}
