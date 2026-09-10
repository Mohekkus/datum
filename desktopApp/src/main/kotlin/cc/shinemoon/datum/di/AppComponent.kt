package cc.shinemoon.datum.di

import cc.shinemoon.datum.occt.OcctRepository
import javax.inject.Singleton

@Singleton
interface AppComponent {
    fun occtRepository(): OcctRepository
}