package com.apurebase.kgraphql


val schema = BenchmarkSchema.create {  }

fun main(vararg args: String){
    while(true){
        println(schema.executeBlocking("{one{name, quantity, active}}"))
        println(schema.executeBlocking("{two(name : \"FELLA\"){range{start, endInclusive}}}"))
        println(schema.executeBlocking("{three{id}}"))
        Thread.sleep(10)
    }
}