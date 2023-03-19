package com.me.harris.awesomelib


import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore


// https://github.com/DylanCaiCoding/Longan/blob/master/longan/src/main/java/com/dylanc/longan/ViewModel.kt
val applicationViewModelStore by lazy { ViewModelStore() }

@MainThread
inline fun <reified VM : ViewModel> ComponentActivity.applicationViewModels(
    noinline factoryProducer: () -> ViewModelProvider.Factory = { defaultViewModelProviderFactory }
): Lazy<VM> =
    createApplicationViewModelLazy(factoryProducer)

@MainThread
inline fun <reified VM : ViewModel> Fragment.applicationViewModels(
    noinline factoryProducer: () -> ViewModelProvider.Factory = { defaultViewModelProviderFactory }
): Lazy<VM> =
    createApplicationViewModelLazy(factoryProducer)

@MainThread
inline fun <reified VM : ViewModel> createApplicationViewModelLazy(
    noinline factoryProducer: () -> ViewModelProvider.Factory
) =
    ViewModelLazy(VM::class, { applicationViewModelStore }, factoryProducer)