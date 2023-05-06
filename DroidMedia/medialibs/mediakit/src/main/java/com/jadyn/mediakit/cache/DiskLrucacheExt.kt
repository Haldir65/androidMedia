package com.jadyn.mediakit.cache

import java.io.IOException

/**
 *@version:
 *@FileDescription:
 *@Author:Jing
 *@Since:2019/3/1
 *@ChangeList:
 */
fun com.jadyn.mediakit.cache.DiskLruCache.takeSnap(key: String): com.jadyn.mediakit.cache.DiskLruCache.Snapshot? {
    return try {
        get(key)
    } catch (e: IOException) {
        null
    }
}