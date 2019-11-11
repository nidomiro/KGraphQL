package com.apurebase.kgraphql.schema.model

import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf


val KType.isGenericType: Boolean
    get() = this.arguments.isNotEmpty()

val KType.isFlow: Boolean
    get() = (this.classifier as? KClass<*>)?.isSubclassOf(Flow::class) ?: false

val <R> KFunction<R>.returnsGenericType: Boolean
    get() = this.returnType.isGenericType

val <R> KFunction<R>.returnsFlow: Boolean
    get() = this.returnType.isFlow

