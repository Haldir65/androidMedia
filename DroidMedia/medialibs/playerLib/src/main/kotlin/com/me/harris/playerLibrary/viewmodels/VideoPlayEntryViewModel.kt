package com.me.harris.playerLibrary.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.me.harris.awesomelib.ApplicationContextProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VideoPlayEntryViewModel @Inject constructor(val handle:SavedStateHandle):  ViewModel() {

    @Inject
    lateinit var contextProvider:ApplicationContextProvider

    fun doStuff(){
        println(contextProvider.context.applicationContext.applicationInfo.dataDir)
    }
}