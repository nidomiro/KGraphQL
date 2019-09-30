package com.apurebase.kgraphqlN

class KGraphQL {
    companion object {
        fun schema(init: SchemaBuilder.() -> Unit): Schema = SchemaBuilder(init).build()
    }
}
