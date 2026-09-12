package cc.shinemoon.datum.di

import contracts.ViewModelFactory
import cc.shinemoon.datum.viewmodel.PresetViewModelFactory

internal lateinit var viewModelFactory: ViewModelFactory
internal lateinit var presetViewModelFactory: PresetViewModelFactory

fun initDependencies(
    factory: ViewModelFactory,
    presetFactory: PresetViewModelFactory,
) {
    viewModelFactory = factory
    presetViewModelFactory = presetFactory
}
