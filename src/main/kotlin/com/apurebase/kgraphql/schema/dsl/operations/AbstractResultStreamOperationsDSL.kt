package com.apurebase.kgraphql.schema.dsl.operations

import com.apurebase.kgraphql.schema.model.FunctionWrapper
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KFunction

abstract class AbstractResultStreamOperationsDSL(name: String) : AbstractOperationDSL(name) {

    fun <T> KFunction<Flow<T>>.toResolver() = resolver(FunctionWrapper.on(this))

    fun <T> resolver(function: suspend () -> Flow<T>) =
        resolver(FunctionWrapper.on(function))

    fun <T, R> resolver(function: suspend (R) -> Flow<T>) =
        resolver(FunctionWrapper.on(function))

    fun <T, R, E> resolver(function: suspend (R, E) -> Flow<T>) =
        resolver(FunctionWrapper.on(function))

    fun <T, R, E, W> resolver(function: suspend (R, E, W) -> Flow<T>) =
        resolver(FunctionWrapper.on(function))

    fun <T, R, E, W, Q> resolver(function: suspend (R, E, W, Q) -> Flow<T>) =
        resolver(FunctionWrapper.on(function))

    fun <T, R, E, W, Q, A> resolver(function: suspend (R, E, W, Q, A) -> Flow<T>) =
        resolver(FunctionWrapper.on(function))

    fun <T, R, E, W, Q, A, S> resolver(function: suspend (R, E, W, Q, A, S) -> Flow<T>) =
        resolver(FunctionWrapper.on(function))

    fun <T, R, E, W, Q, A, S, B> resolver(function: suspend (R, E, W, Q, A, S, B) -> Flow<T>) =
        resolver(FunctionWrapper.on(function))

    fun <T, R, E, W, Q, A, S, B, U> resolver(function: suspend (R, E, W, Q, A, S, B, U) -> Flow<T>) =
        resolver(FunctionWrapper.on(function))

    fun <T, R, E, W, Q, A, S, B, U, C> resolver(function: suspend (R, E, W, Q, A, S, B, U, C) -> Flow<T>) =
        resolver(FunctionWrapper.on(function))
}