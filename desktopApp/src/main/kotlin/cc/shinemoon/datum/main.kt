package cc.shinemoon.datum

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import cc.shinemoon.datum.di.DaggerAppComponent
import cc.shinemoon.datum.di.initDependencies

fun main() {
    val appComponent = DaggerAppComponent.create()
    initDependencies(appComponent)

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "datum",
        ) {
            App()
        }
    }
}