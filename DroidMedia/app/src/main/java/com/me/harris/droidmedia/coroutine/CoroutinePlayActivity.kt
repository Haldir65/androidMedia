package com.me.harris.droidmedia.coroutine

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.me.harris.droidmedia.R
import kotlinx.coroutines.*
import okhttp3.Dispatcher

class CoroutinePlayActivity:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coroutine_play)
        test2()
    }

    private fun log(info:String){
        val TAG = "=A="
        Log.i(TAG,info)
    }

    private fun test1(){
        GlobalScope.launch { // 创建并启动一个新的协程
            delay(1000L)
            log("World!")
        }
        log("Hello,") // 主线程中的代码会立即执行
        runBlocking {     // 启动一个协程, 但是这个表达式阻塞了主线程
            delay(2000L)  // 我们延迟 2 秒来保证 JVM 的存活
        }
    }

    private fun test2(){
        runBlocking {
            val job = launch(Dispatchers.IO) {
                repeat(1000) { i ->
                    log("job: I'm sleeping $i ...")
                    delay(500L)
                }
            }
            delay(1300L) // 延迟一段时间
            log("main: I'm tired of waiting!")
            job.cancel() // 取消该作业
            job.join() // 等待作业执行结束
            log("main: Now I can quit.")
        }
    }
}