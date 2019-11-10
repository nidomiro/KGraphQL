package com.apurebase.kgraphql

import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.jvmErasure

internal fun <T : Any> KClass<T>.defaultKQLTypeName() = this.simpleName!!

internal fun KType.defaultKQLTypeName() = this.jvmErasure.defaultKQLTypeName()

internal fun String.dropQuotes() : String = if(isLiteral()) drop(1).dropLast(1) else this

internal fun String.isLiteral() : Boolean = startsWith('\"') && endsWith('\"')

internal fun KParameter.isNullable() = type.isMarkedNullable

internal fun KParameter.isNotNullable() = !type.isMarkedNullable

internal fun KClass<*>.isIterable() = isSubclassOf(Iterable::class)

internal fun KType.isIterable() = jvmErasure.isIterable() || toString().startsWith("kotlin.Array")

internal fun KType.getIterableElementType(): KType? {
    require(isIterable()) { "KType $this is not collection type" }
    return arguments.firstOrNull()?.type ?: throw NoSuchElementException("KType $this has no type arguments")
}


internal fun not(boolean: Boolean) = !boolean

