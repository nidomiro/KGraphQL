package com.apurebase.kgraphqlN

interface Schema {
    fun execute(request: String, variables: String = ""): String
}
