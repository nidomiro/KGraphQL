package com.apurebase.kgraphql.schema.dsl.operations

import com.apurebase.kgraphql.schema.model.FunctionWrapper
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KFunction

abstract class AbstractResultStreamOperationsDSL(name: String) : AbstractOperationDSL(name) {

    fun <R> KFunction<Flow<R>>.toResolver() = resolver(FunctionWrapper.on(this))

    fun <R> resolver(function: suspend () -> Flow<R>) =
        resolver(FunctionWrapper.on(function))

    fun <R, T> resolver(function: suspend (T) -> Flow<R>) =
        resolver(FunctionWrapper.on(function))

    fun <R, T, E> resolver(function: suspend (T, E) -> Flow<R>) =
        resolver(FunctionWrapper.on(function))

    fun <R, T, E, W> resolver(function: suspend (T, E, W) -> Flow<R>) =
        resolver(FunctionWrapper.on(function))

    fun <R, T, E, W, Q> resolver(function: suspend (T, E, W, Q) -> Flow<R>) =
        resolver(FunctionWrapper.on(function))

    fun <R, T, E, W, Q, A> resolver(function: suspend (T, E, W, Q, A) -> Flow<R>) =
        resolver(FunctionWrapper.on(function))

    fun <R, T, E, W, Q, A, S> resolver(function: suspend (T, E, W, Q, A, S) -> Flow<R>) =
        resolver(FunctionWrapper.on(function))

    fun <R, T, E, W, Q, A, S, B> resolver(function: suspend (T, E, W, Q, A, S, B) -> Flow<R>) =
        resolver(FunctionWrapper.on(function))

    fun <R, T, E, W, Q, A, S, B, U> resolver(function: suspend (T, E, W, Q, A, S, B, U) -> Flow<R>) =
        resolver(FunctionWrapper.on(function))

    fun <R, T, E, W, Q, A, S, B, U, C> resolver(function: suspend (T, E, W, Q, A, S, B, U, C) -> Flow<R>) =
        resolver(FunctionWrapper.on(function))
}