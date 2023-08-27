package com.me.harris.extractframe.finale

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.me.harris.extractframe.finale.creator.ExtractConfig
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

internal class FrameExtractFinaleViewModel(application: Application) :AndroidViewModel(application){


    private val _states = MutableStateFlow<String>("")
    val states = _states.asStateFlow()

    private val _events = MutableSharedFlow<Event>()
    val events = _events.asSharedFlow()

    fun startExtract(config: ExtractConfig){
        viewModelScope.launch {
            val context = getApplication<Application>()
            _events.tryEmit(Event.ExtractStartEvent)

        }
    }
}
