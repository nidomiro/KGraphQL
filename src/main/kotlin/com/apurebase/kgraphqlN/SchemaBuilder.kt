package com.apurebase.kgraphqlN

class SchemaBuilder(private val init: SchemaBuilder.() -> Unit) {

    internal fun build(): Schema {
        init()
        return TODO("Not implemented")
    }

}
