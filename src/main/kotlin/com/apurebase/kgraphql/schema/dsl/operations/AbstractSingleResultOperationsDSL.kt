package com.apurebase.kgraphql.schema.dsl.operations

import com.apurebase.kgraphql.schema.model.FunctionWrapper
import kotlin.reflect.KFunction

abstract class AbstractSingleResultOperationsDSL(name: String) : AbstractOperationDSL(name) {

    fun <R> KFunction<R>.toResolver() = resolver(FunctionWrapper.on(this))

    fun <R> resolver(function: suspend () -> R) = resolver(FunctionWrapper.on(function))

    fun <R, T> resolver(function: suspend (T) -> R) = resolver(FunctionWrapper.on(function))

    fun <R, T, E> resolver(function: suspend (T, E) -> R) = resolver(FunctionWrapper.on(function))

    fun <R, T, E, W> resolver(function: suspend (T, E, W) -> R) = resolver(FunctionWrapper.on(function))

    fun <R, T, E, W, Q> resolver(function: suspend (T, E, W, Q) -> R) = resolver(FunctionWrapper.on(function))

    fun <R, T, E, W, Q, A> resolver(function: suspend (T, E, W, Q, A) -> R) = resolver(FunctionWrapper.on(function))

    fun <R, T, E, W, Q, A, S> resolver(function: suspend (T, E, W, Q, A, S) -> R) =
        resolver(FunctionWrapper.on(function))

    fun <R, T, E, W, Q, A, S, B> resolver(function: suspend (T, E, W, Q, A, S, B) -> R) =
        resolver(FunctionWrapper.on(function))

    fun <R, T, E, W, Q, A, S, B, U> resolver(function: suspend (T, E, W, Q, A, S, B, U) -> R) =
        resolver(FunctionWrapper.on(function))

    fun <R, T, E, W, Q, A, S, B, U, C> resolver(function: suspend (T, E, W, Q, A, S, B, U, C) -> R) = resolver(
        FunctionWrapper.on(function)
    )
}