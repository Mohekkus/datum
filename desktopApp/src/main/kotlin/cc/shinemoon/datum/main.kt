package cc.shinemoon.datum

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import cc.shinemoon.datum.di.DaggerAppComponent
import cc.shinemoon.datum.di.initDependencies

fun main() {
    val appComponent = DaggerAppComponent.create()
    initDependencies(appComponent, appComponent.presetViewModelFactory())

    application {
        val windowState = rememberWindowState(
            size = DpSize(width = 1280.dp, height = 820.dp)
        )
        Window(
            onCloseRequest = ::exitApplication,
            title = "datum",
            state = windowState
        ) {
            App()
        }
    }
}
