@file:Suppress("UNCHECKED_CAST")

package com.apurebase.kgraphql.schema.model

import com.apurebase.kgraphql.schema.SchemaException
import com.apurebase.kgraphql.schema.structure.validateName
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.reflect.KFunction
import kotlin.reflect.KType
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.extensionReceiverParameter
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.reflect

/**
 * FunctionWrapper is common interface for classes storing functions registered in schema by server code.
 * Only up to 9 arguments are supported, because kotlin-reflect doesn't support
 * invoking lambdas, local and anonymous functions yet, making implementation severely limited.
 */
interface FunctionWrapper<R> {
    //lots of boilerplate here, because kotlin-reflect doesn't support invoking lambdas, local and anonymous functions yet
    companion object {
        //FIXME: Add Wrapper for (...) -> T to (...) -> Flow<T> for Compatibility
        fun <T> on(function: KFunction<Flow<T>>): FunctionWrapper<T> = ArityN(function)

        fun <T> on(function: suspend () -> Flow<T>): FunctionWrapper<T> = ArityZero(function)

        fun <T, R> on(function: suspend (R) -> Flow<T>) = ArityOne(function, false)

        fun <T, R> on(function: suspend (R) -> Flow<T>, hasReceiver: Boolean = false) = ArityOne(function, hasReceiver)

        fun <T, R, E> on(function: suspend (R, E) -> Flow<T>, hasReceiver: Boolean = false) =
            ArityTwo(function, hasReceiver)

        fun <T, R, E, W> on(function: suspend (R, E, W) -> Flow<T>, hasReceiver: Boolean = false) =
            ArityThree(function, hasReceiver)

        fun <T, R, E, W, Q> on(function: suspend (R, E, W, Q) -> Flow<T>, hasReceiver: Boolean = false) =
            ArityFour(function, hasReceiver)

        fun <T, R, E, W, Q, A> on(function: suspend (R, E, W, Q, A) -> Flow<T>, hasReceiver: Boolean = false) =
            ArityFive(function, hasReceiver)

        fun <T, R, E, W, Q, A, S> on(function: suspend (R, E, W, Q, A, S) -> Flow<T>, hasReceiver: Boolean = false) =
            AritySix(function, hasReceiver)

        fun <T, R, E, W, Q, A, S, G> on(
            function: suspend (R, E, W, Q, A, S, G) -> Flow<T>,
            hasReceiver: Boolean = false
        ) = AritySeven(function, hasReceiver)

        fun <T, R, E, W, Q, A, S, G, H> on(
            function: suspend (R, E, W, Q, A, S, G, H) -> Flow<T>,
            hasReceiver: Boolean = false
        ) = ArityEight(function, hasReceiver)

        fun <T, R, E, W, Q, A, S, G, H, J> on(
            function: suspend (R, E, W, Q, A, S, G, H, J) -> Flow<T>,
            hasReceiver: Boolean = false
        ) = ArityNine(function, hasReceiver)

    }

    val kFunction: KFunction<Flow<R>>

    suspend fun invoke(vararg args: Any?): Flow<R>?

    fun arity(): Int

    /**
     * denotes whether function is called with receiver argument.
     * Receiver argument in GraphQL is somewhat similar to kotlin receivers:
     * its value is passed by framework, usually it is parent of function property with [FunctionWrapper]
     * Receiver argument is omitted in schema, and cannot be stated in query document.
     */
    val hasReceiver: Boolean

    val argumentsDescriptor: Map<String, KType>

    abstract class Base<R> : FunctionWrapper<R> {

        private fun createArgumentsDescriptor(): Map<String, KType> {
            return valueParameters().associate { parameter ->
                val parameterName = parameter.name
                    ?: throw SchemaException("Cannot handle nameless argument on function: $kFunction")

                validateName(parameterName)
                parameterName to parameter.type
            }
        }

        override val argumentsDescriptor: Map<String, KType> by lazy { createArgumentsDescriptor() }

        override suspend fun invoke(vararg args: Any?): Flow<R>? {
            require(args.size == arity()) { "This function needs exactly ${arity()} arguments" }
            return invokeInternal(*args)
        }

        protected open suspend fun invokeInternal(vararg args: Any?): Flow<R>? {
            return flow { }
        }


    }

    /**
     * returns list of function parameters without receiver
     */
    fun valueParameters(): List<kotlin.reflect.KParameter> {
        return kFunction.valueParameters.let {
            if (hasReceiver) it.drop(1) else it
        }
    }

    class ArityN<R>(override val kFunction: KFunction<Flow<R>>) : Base<R>() {

        override fun arity() = kFunction.parameters.size
        override val hasReceiver: Boolean
            get() = kFunction.extensionReceiverParameter != null

        override suspend fun invoke(vararg args: Any?): Flow<R>? = kFunction.callSuspend(*args)
    }

    class ArityZero<R>(val implementation: suspend () -> Flow<R>, override val hasReceiver: Boolean = false) :
        Base<R>() {

        override val kFunction: KFunction<Flow<R>> by lazy { implementation.reflect()!! }
        override fun arity(): Int = 0

        override suspend fun invokeInternal(vararg args: Any?): Flow<R>? = implementation()
    }

    class ArityOne<R, T>(val implementation: suspend (T) -> Flow<R>, override val hasReceiver: Boolean) : Base<R>() {

        override val kFunction: KFunction<Flow<R>> by lazy { implementation.reflect()!! }
        override fun arity(): Int = 1

        override suspend fun invokeInternal(vararg args: Any?): Flow<R>? = implementation(args[0] as T)
    }

    class ArityTwo<R, T, E>(val implementation: suspend (T, E) -> Flow<R>, override val hasReceiver: Boolean) :
        Base<R>() {

        override val kFunction: KFunction<Flow<R>> by lazy { implementation.reflect()!! }
        override fun arity(): Int = 2

        override suspend fun invokeInternal(vararg args: Any?): Flow<R>? = implementation(args[0] as T, args[1] as E)
    }

    class ArityThree<R, T, E, W>(val implementation: suspend (T, E, W) -> Flow<R>, override val hasReceiver: Boolean) :
        Base<R>() {

        override val kFunction: KFunction<Flow<R>> by lazy { implementation.reflect()!! }
        override fun arity(): Int = 3

        override suspend fun invokeInternal(vararg args: Any?): Flow<R>? =
            implementation(args[0] as T, args[1] as E, args[2] as W)
    }

    class ArityFour<R, T, E, W, Q>(
        val implementation: suspend (T, E, W, Q) -> Flow<R>,
        override val hasReceiver: Boolean
    ) : Base<R>() {

        override val kFunction: KFunction<Flow<R>> by lazy { implementation.reflect()!! }
        override fun arity(): Int = 4

        override suspend fun invokeInternal(vararg args: Any?): Flow<R>? =
            implementation(args[0] as T, args[1] as E, args[2] as W, args[3] as Q)
    }

    class ArityFive<R, T, E, W, Q, A>(
        val implementation: suspend (T, E, W, Q, A) -> Flow<R>,
        override val hasReceiver: Boolean
    ) : Base<R>() {

        override val kFunction: KFunction<Flow<R>> by lazy { implementation.reflect()!! }
        override fun arity(): Int = 5

        override suspend fun invokeInternal(vararg args: Any?): Flow<R>? =
            implementation(args[0] as T, args[1] as E, args[2] as W, args[3] as Q, args[4] as A)
    }

    class AritySix<R, T, E, W, Q, A, S>(
        val implementation: suspend (T, E, W, Q, A, S) -> Flow<R>,
        override val hasReceiver: Boolean
    ) : Base<R>() {

        override val kFunction: KFunction<Flow<R>> by lazy { implementation.reflect()!! }
        override fun arity(): Int = 6

        override suspend fun invokeInternal(vararg args: Any?): Flow<R>? =
            implementation(args[0] as T, args[1] as E, args[2] as W, args[3] as Q, args[4] as A, args[5] as S)
    }

    class AritySeven<R, T, E, W, Q, A, S, D>(
        val implementation: suspend (T, E, W, Q, A, S, D) -> Flow<R>,
        override val hasReceiver: Boolean
    ) : Base<R>() {

        override val kFunction: KFunction<Flow<R>> by lazy { implementation.reflect()!! }
        override fun arity(): Int = 7

        override suspend fun invokeInternal(vararg args: Any?): Flow<R>? =
            implementation(
                args[0] as T,
                args[1] as E,
                args[2] as W,
                args[3] as Q,
                args[4] as A,
                args[5] as S,
                args[6] as D
            )
    }

    class ArityEight<R, T, E, W, Q, A, S, D, F>(
        val implementation: suspend (T, E, W, Q, A, S, D, F) -> Flow<R>,
        override val hasReceiver: Boolean
    ) : Base<R>() {

        override val kFunction: KFunction<Flow<R>> by lazy { implementation.reflect()!! }
        override fun arity(): Int = 8

        override suspend fun invokeInternal(vararg args: Any?): Flow<R>? =
            implementation(
                args[0] as T,
                args[1] as E,
                args[2] as W,
                args[3] as Q,
                args[4] as A,
                args[5] as S,
                args[6] as D,
                args[7] as F
            )
    }

    class ArityNine<R, T, E, W, Q, A, S, D, F, G>(
        val implementation: suspend (T, E, W, Q, A, S, D, F, G) -> Flow<R>,
        override val hasReceiver: Boolean
    ) : Base<R>() {

        override val kFunction: KFunction<Flow<R>> by lazy { implementation.reflect()!! }
        override fun arity(): Int = 9

        override suspend fun invokeInternal(vararg args: Any?): Flow<R>? =
            implementation(
                args[0] as T,
                args[1] as E,
                args[2] as W,
                args[3] as Q,
                args[4] as A,
                args[5] as S,
                args[6] as D,
                args[7] as F,
                args[8] as G
            )
    }
}
