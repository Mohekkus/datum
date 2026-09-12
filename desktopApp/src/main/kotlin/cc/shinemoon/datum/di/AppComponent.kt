package cc.shinemoon.datum.di

import cc.shinemoon.datum.di.module.OcctModule
import cc.shinemoon.datum.viewmodel.OcctViewModel
import contracts.OcctViewModelContract
import contracts.ViewModelFactory
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [OcctModule::class])
interface AppComponent : ViewModelFactory {
    fun occtViewModelImpl(): OcctViewModel
    override fun occtViewModel(): OcctViewModelContract = occtViewModelImpl()
}