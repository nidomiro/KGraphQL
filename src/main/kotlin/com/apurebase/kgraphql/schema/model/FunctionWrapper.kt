@file:Suppress("UNCHECKED_CAST")

package com.apurebase.kgraphql.schema.model

import com.apurebase.kgraphql.schema.SchemaException
import com.apurebase.kgraphql.schema.structure.validateName
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
        fun <R> on(function: KFunction<R>): FunctionWrapper<R> = ArityN(function)

        fun <R> on(function: suspend () -> R): FunctionWrapper<R> = ArityZero(function)

        fun <R, T> on(function: suspend (T) -> R) = ArityOne(function, false)

        fun <R, T> on(function: suspend (T) -> R, hasReceiver: Boolean = false) = ArityOne(function, hasReceiver)

        fun <R, T, E> on(function: suspend (T, E) -> R, hasReceiver: Boolean = false) =
            ArityTwo(function, hasReceiver)

        fun <R, T, E, W> on(function: suspend (T, E, W) -> R, hasReceiver: Boolean = false) =
            ArityThree(function, hasReceiver)

        fun <R, T, E, W, Q> on(function: suspend (T, E, W, Q) -> R, hasReceiver: Boolean = false) =
            ArityFour(function, hasReceiver)

        fun <R, T, E, W, Q, A> on(function: suspend (T, E, W, Q, A) -> R, hasReceiver: Boolean = false) =
            ArityFive(function, hasReceiver)

        fun <R, T, E, W, Q, A, S> on(function: suspend (T, E, W, Q, A, S) -> R, hasReceiver: Boolean = false) =
            AritySix(function, hasReceiver)

        fun <R, T, E, W, Q, A, S, G> on(
            function: suspend (T, E, W, Q, A, S, G) -> R,
            hasReceiver: Boolean = false
        ) = AritySeven(function, hasReceiver)

        fun <R, T, E, W, Q, A, S, G, H> on(
            function: suspend (T, E, W, Q, A, S, G, H) -> R,
            hasReceiver: Boolean = false
        ) = ArityEight(function, hasReceiver)

        fun <R, T, E, W, Q, A, S, G, H, J> on(
            function: suspend (T, E, W, Q, A, S, G, H, J) -> R,
            hasReceiver: Boolean = false
        ) = ArityNine(function, hasReceiver)

    }

    val kFunction: KFunction<R>

    suspend fun invoke(vararg args: Any?): R?

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

        override suspend fun invoke(vararg args: Any?): R? {
            require(args.size == arity()) { "This function needs exactly ${arity()} arguments" }
            return invokeInternal(*args)
        }

        protected open suspend fun invokeInternal(vararg args: Any?): R? {
            return null
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

    class ArityN<R>(override val kFunction: KFunction<R>) : Base<R>() {

        override fun arity() = kFunction.parameters.size
        override val hasReceiver: Boolean
            get() = kFunction.extensionReceiverParameter != null

        override suspend fun invoke(vararg args: Any?): R? = kFunction.callSuspend(*args)
    }

    class ArityZero<R>(val implementation: suspend () -> R, override val hasReceiver: Boolean = false) :
        Base<R>() {

        override val kFunction: KFunction<R> by lazy { implementation.reflect()!! }
        override fun arity(): Int = 0

        override suspend fun invokeInternal(vararg args: Any?): R? = implementation()
    }

    class ArityOne<R, T>(val implementation: suspend (T) -> R, override val hasReceiver: Boolean) : Base<R>() {

        override val kFunction: KFunction<R> by lazy { implementation.reflect()!! }
        override fun arity(): Int = 1

        override suspend fun invokeInternal(vararg args: Any?): R? = implementation(args[0] as T)
    }

    class ArityTwo<R, T, E>(val implementation: suspend (T, E) -> R, override val hasReceiver: Boolean) :
        Base<R>() {

        override val kFunction: KFunction<R> by lazy { implementation.reflect()!! }
        override fun arity(): Int = 2

        override suspend fun invokeInternal(vararg args: Any?): R? = implementation(args[0] as T, args[1] as E)
    }

    class ArityThree<R, T, E, W>(val implementation: suspend (T, E, W) -> R, override val hasReceiver: Boolean) :
        Base<R>() {

        override val kFunction: KFunction<R> by lazy { implementation.reflect()!! }
        override fun arity(): Int = 3

        override suspend fun invokeInternal(vararg args: Any?): R? =
            implementation(args[0] as T, args[1] as E, args[2] as W)
    }

    class ArityFour<R, T, E, W, Q>(
        val implementation: suspend (T, E, W, Q) -> R,
        override val hasReceiver: Boolean
    ) : Base<R>() {

        override val kFunction: KFunction<R> by lazy { implementation.reflect()!! }
        override fun arity(): Int = 4

        override suspend fun invokeInternal(vararg args: Any?): R? =
            implementation(args[0] as T, args[1] as E, args[2] as W, args[3] as Q)
    }

    class ArityFive<R, T, E, W, Q, A>(
        val implementation: suspend (T, E, W, Q, A) -> R,
        override val hasReceiver: Boolean
    ) : Base<R>() {

        override val kFunction: KFunction<R> by lazy { implementation.reflect()!! }
        override fun arity(): Int = 5

        override suspend fun invokeInternal(vararg args: Any?): R? =
            implementation(args[0] as T, args[1] as E, args[2] as W, args[3] as Q, args[4] as A)
    }

    class AritySix<R, T, E, W, Q, A, S>(
        val implementation: suspend (T, E, W, Q, A, S) -> R,
        override val hasReceiver: Boolean
    ) : Base<R>() {

        override val kFunction: KFunction<R> by lazy { implementation.reflect()!! }
        override fun arity(): Int = 6

        override suspend fun invokeInternal(vararg args: Any?): R? =
            implementation(args[0] as T, args[1] as E, args[2] as W, args[3] as Q, args[4] as A, args[5] as S)
    }

    class AritySeven<R, T, E, W, Q, A, S, D>(
        val implementation: suspend (T, E, W, Q, A, S, D) -> R,
        override val hasReceiver: Boolean
    ) : Base<R>() {

        override val kFunction: KFunction<R> by lazy { implementation.reflect()!! }
        override fun arity(): Int = 7

        override suspend fun invokeInternal(vararg args: Any?): R? =
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
        val implementation: suspend (T, E, W, Q, A, S, D, F) -> R,
        override val hasReceiver: Boolean
    ) : Base<R>() {

        override val kFunction: KFunction<R> by lazy { implementation.reflect()!! }
        override fun arity(): Int = 8

        override suspend fun invokeInternal(vararg args: Any?): R? =
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
        val implementation: suspend (T, E, W, Q, A, S, D, F, G) -> R,
        override val hasReceiver: Boolean
    ) : Base<R>() {

        override val kFunction: KFunction<R> by lazy { implementation.reflect()!! }
        override fun arity(): Int = 9

        override suspend fun invokeInternal(vararg args: Any?): R? =
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
