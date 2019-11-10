package com.apurebase.kgraphql


val schema = BenchmarkSchema.create {  }

fun main(vararg args: String){
    while(true){
        println(schema.executeBlockingGetOne("{one{name, quantity, active}}"))
        println(schema.executeBlockingGetOne("{two(name : \"FELLA\"){range{start, endInclusive}}}"))
        println(schema.executeBlockingGetOne("{three{id}}"))
        Thread.sleep(10)
    }
}