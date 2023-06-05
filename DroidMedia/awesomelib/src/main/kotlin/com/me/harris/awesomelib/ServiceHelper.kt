package com.me.harris.awesomelib

import java.util.ServiceLoader

object ServiceHelper {

    val TAG = ServiceHelper::class.java.name
    fun <T> getServices(interfaceClass: Class<T>?): List<T> {
        val loader = ServiceLoader.load(interfaceClass)
        val iterator: Iterator<T> = loader.iterator()
        val list: MutableList<T> = ArrayList()
        while (iterator.hasNext()) {
            val t: T? = iterator.next()
            if (t != null) {
                list.add(t)
            }
        }
        return list
    }

    fun <T> getService(interfaceClass: Class<T>?): T? {
        val loader = ServiceLoader.load(interfaceClass)
        val iterator: Iterator<T> = loader.iterator()
        return if (iterator.hasNext()) {
            iterator.next()
        } else null
    }

//    inline operator fun<reified T:Any> get():T?{
//        return getService(T::class.java)
//    }
}