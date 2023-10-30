package com.me.harris.simdjson

object JsonJni {

    init {
        System.loadLibrary("simd-json")
    }


    external fun loadJsonFile(filepath:String)

}
