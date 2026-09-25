package cc.shinemoon.datum.di

import cc.shinemoon.datum.di.module.AppModule
import cc.shinemoon.datum.viewmodel.OcctViewModel
import cc.shinemoon.datum.viewmodel.PresetViewModelFactory
import cc.shinemoon.datumabase.database.DatabaseInitializer
import contracts.OcctViewModelContract
import contracts.ViewModelFactory
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent : ViewModelFactory {
    fun occtViewModelImpl(): OcctViewModel
    override fun occtViewModel(): OcctViewModelContract = occtViewModelImpl()

    fun presetViewModelFactory(): PresetViewModelFactory
    fun databaseInitializer(): DatabaseInitializer
}
