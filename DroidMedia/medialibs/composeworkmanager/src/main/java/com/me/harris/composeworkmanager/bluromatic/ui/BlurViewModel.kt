/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.me.harris.composeworkmanager.bluromatic.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.work.WorkInfo
import com.me.harris.awesomelib.ServiceHelper
import com.me.harris.composeworkmanager.KEY_IMAGE_URI
import com.me.harris.composeworkmanager.bluromatic.data.BlurAmountData
import com.me.harris.composeworkmanager.bluromatic.data.BluromaticRepository
import com.me.harris.composeworkmanager.bluromatic.data.DefaultAppContainer
import com.me.harris.serviceapi.applicationGainer.IApplicationLike
import kotlinx.coroutines.flow.*

/**
 * [BlurViewModel] starts and stops the WorkManger and applies blur to the image. Also updates the
 * visibility states of the buttons depending on the states of the WorkManger.
 */
class BlurViewModel(private val bluromaticRepository: BluromaticRepository) : ViewModel() {

    internal val blurAmount = BlurAmountData.blurAmount

    val blurUiState: StateFlow<BlurUiState> = bluromaticRepository.outputWorkInfo
        .map { info ->
            val outputImageUri = info.outputData.getString(KEY_IMAGE_URI)
            when {
                info.state.isFinished && !outputImageUri.isNullOrEmpty() -> {
                    BlurUiState.Complete(outputUri = outputImageUri)
                }
                info.state == WorkInfo.State.CANCELLED -> {
                    BlurUiState.Default
                }
                else -> BlurUiState.Loading
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = BlurUiState.Default
        )

    /**
     * Call the method from repository to create the WorkRequest to apply the blur
     * and save the resulting image
     * @param blurLevel The amount to blur the image
     */
    fun applyBlur(blurLevel: Int) {
        bluromaticRepository.applyBlur(blurLevel)
    }

    /**
     * Call method from repository to cancel any ongoing WorkRequest
     * */
    fun cancelWork() {
        bluromaticRepository.cancelWork()
    }

    /**
     * Factory for [BlurViewModel] that takes [BluromaticRepository] as a dependency
     */
    companion object {
        val bluromaticRepository by lazy {
            val application = requireNotNull( ServiceHelper.getService(IApplicationLike::class.java)?.application)
            val bluromaticRepository =  DefaultAppContainer(application).bluromaticRepository
            bluromaticRepository
        }

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                BlurViewModel(
                    bluromaticRepository = bluromaticRepository
                )
            }
        }

    }
}

sealed interface BlurUiState {
    object Default : BlurUiState
    object Loading : BlurUiState
    data class Complete(val outputUri: String) : BlurUiState
}
