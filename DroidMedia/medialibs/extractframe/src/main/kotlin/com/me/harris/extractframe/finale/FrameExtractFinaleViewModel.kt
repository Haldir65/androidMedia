package com.me.harris.extractframe.finale

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.me.harris.extractframe.finale.creator.ExtractConfig
import com.me.harris.extractframe.finale.creator.ExtractLegion
import com.me.harris.extractframe.finale.creator.ExtractUnit
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

internal class FrameExtractFinaleViewModel(application: Application) :AndroidViewModel(application){


    private val _states = MutableStateFlow<String>("")
    val states = _states.asStateFlow()

    private val _events = MutableSharedFlow<Event>()
    val events = _events.asSharedFlow()

    fun startExtract(config: ExtractConfig){
        viewModelScope.launch (CoroutineExceptionHandler { e,a ->
            Log.e("=A=","critical ${a.stackTraceToString()}")
        }){
            val start = System.currentTimeMillis()
            val context = getApplication<Application>()
            _events.emit(Event.ExtractStartEvent)
            val hub = ExtractLegion(config)
            val units = hub.distributeIntoMultipleUnits()
            units.mapIndexed { index, m ->
                ExtractUnit(index,config,m.range,context)
            }.map { a->
                async(Dispatchers.IO) {
//                    a.doingExtractAsync()
                    a.doingExtract()
                }
            }.awaitAll()
            val cost = System.currentTimeMillis() - start
            Log.e("=A=","total time cost is ${cost}")
            _events.emit(Event.ExtractDoneEvent(cost,config))
        }
    }
}
