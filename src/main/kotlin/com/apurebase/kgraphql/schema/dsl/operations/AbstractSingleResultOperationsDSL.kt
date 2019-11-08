package com.apurebase.kgraphql.schema.dsl.operations

import com.apurebase.kgraphql.schema.model.FunctionWrapper
import kotlin.reflect.KFunction

abstract class AbstractSingleResultOperationsDSL(name: String) : AbstractOperationDSL(name) {

    fun <T> KFunction<T>.toResolver() = resolver(FunctionWrapper.on(this))

    fun <T> resolver(function: suspend () -> T) = resolver(FunctionWrapper.on(function))

    fun <T, R> resolver(function: suspend (R) -> T) = resolver(FunctionWrapper.on(function))

    fun <T, R, E> resolver(function: suspend (R, E) -> T) = resolver(FunctionWrapper.on(function))

    fun <T, R, E, W> resolver(function: suspend (R, E, W) -> T) = resolver(FunctionWrapper.on(function))

    fun <T, R, E, W, Q> resolver(function: suspend (R, E, W, Q) -> T) = resolver(FunctionWrapper.on(function))

    fun <T, R, E, W, Q, A> resolver(function: suspend (R, E, W, Q, A) -> T) = resolver(FunctionWrapper.on(function))

    fun <T, R, E, W, Q, A, S> resolver(function: suspend (R, E, W, Q, A, S) -> T) =
        resolver(FunctionWrapper.on(function))

    fun <T, R, E, W, Q, A, S, B> resolver(function: suspend (R, E, W, Q, A, S, B) -> T) =
        resolver(FunctionWrapper.on(function))

    fun <T, R, E, W, Q, A, S, B, U> resolver(function: suspend (R, E, W, Q, A, S, B, U) -> T) =
        resolver(FunctionWrapper.on(function))

    fun <T, R, E, W, Q, A, S, B, U, C> resolver(function: suspend (R, E, W, Q, A, S, B, U, C) -> T) = resolver(
        FunctionWrapper.on(function)
    )
}