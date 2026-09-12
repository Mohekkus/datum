package cc.shinemoon.datum.di.module

import cc.shinemoon.datum.repository.OcctRepository
import cc.shinemoon.datum.repository.PresetDatabaseRepository
import cc.shinemoon.datum.usecase.DatabaseUseCase
import cc.shinemoon.datum.usecase.OcctUseCase
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class AppModule {
    @Binds
    @Singleton
    abstract fun bindOcctUseCase(impl: OcctRepository): OcctUseCase

    @Binds
    @Singleton
    abstract fun bindDatabaseUseCase(impl: PresetDatabaseRepository): DatabaseUseCase
}
