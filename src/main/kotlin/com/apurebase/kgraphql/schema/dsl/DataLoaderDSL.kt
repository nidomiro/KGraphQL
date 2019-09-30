package com.apurebase.kgraphql.schema.dsl

import com.apurebase.kgraphql.Context
import com.apurebase.kgraphql.schema.model.FunctionWrapper
import com.apurebase.kgraphql.schema.model.InputValueDef
import com.apurebase.kgraphql.schema.model.PropertyDef
import java.lang.IllegalArgumentException
import java.util.concurrent.ConcurrentHashMap


open class DataLoaderDSL<T, K, R>(
    val name: String,
    block : DataLoaderDSL<T, K, R>.() -> Unit
): LimitedAccessItemDSL<T>(), ResolverDSL.Target {

    init { block() }

    internal lateinit var prepareWrapper : FunctionWrapper<K>
    internal lateinit var loaderWrapper: FunctionWrapper<Map<K, R?>>

    private val inputValues = mutableListOf<InputValueDef<*>>()

    // TODO: Solve this hack with reflection instead
    internal lateinit var returnType: FunctionWrapper<R?>

    fun setReturnType(block: suspend () -> R) {
        returnType = FunctionWrapper.on(block)
    }

    fun loader(block: suspend (List<K>) -> Map<K, R?>) {
        loaderWrapper = FunctionWrapper.on(block, true)
    }

    fun prepare(block: suspend (T) -> K) {
        prepareWrapper = FunctionWrapper.on(block, true)
    }

    fun <E> prepare(block: suspend (T, E) -> K) {
        prepareWrapper = FunctionWrapper.on(block, true)
    }

    fun <E, W> prepare(block: suspend (T, E, W) -> K) {
        prepareWrapper = FunctionWrapper.on(block, true)
    }

    fun <E, W, Q> prepare(block: suspend (T, E, W, Q) -> K) {
        prepareWrapper = FunctionWrapper.on(block, true)
    }

    fun <E, W, Q, A> prepare(block: suspend (T, E, W, Q, A) -> K) {
        prepareWrapper = FunctionWrapper.on(block, true)
    }

    fun <E, W, Q, A, S> prepare(block: suspend (T, E, W, Q, A, S) -> K) {
        prepareWrapper = FunctionWrapper.on(block, true)
    }



    fun accessRule(rule: (T, Context) -> Exception?){
        val accessRuleAdapter: (T?, Context) -> Exception? = { parent, ctx ->
            if (parent != null) rule(parent, ctx) else IllegalArgumentException("Unexpected null parent of kotlin property")
        }
        this.accessRuleBlock = accessRuleAdapter
    }

    fun toKQLProperty() = PropertyDef.DataLoadDef<T, K, R?>(
        name = name,
        description = description,
        accessRule = accessRuleBlock,
        deprecationReason = deprecationReason,
        isDeprecated = isDeprecated,
        inputValues = inputValues,
        loader = loaderWrapper,
        prepare = prepareWrapper,
        returnWrapper = returnType,
        cache = ConcurrentHashMap()
    )


    override fun addInputValues(inputValues: Collection<InputValueDef<*>>) {
        this.inputValues.addAll(inputValues)
    }

}
