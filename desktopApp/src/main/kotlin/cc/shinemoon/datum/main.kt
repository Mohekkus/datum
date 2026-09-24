package cc.shinemoon.datum

import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.rememberWindowState
import cc.shinemoon.datum.di.DaggerAppComponent
import cc.shinemoon.datum.di.initDependencies
import dev.nucleusframework.application.DecoratedWindow
import dev.nucleusframework.application.NucleusBackend
import dev.nucleusframework.application.nucleusApplication
import dev.nucleusframework.window.TitleBar
import androidx.compose.ui.window.ApplicationScope
import kotlin.system.exitProcess

fun main() {
    nucleusApplication(backend = NucleusBackend.Tao) {
        val appComponent = DaggerAppComponent.create()
        initDependencies(appComponent, appComponent.presetViewModelFactory())

        val windowState = rememberWindowState(
            size = DpSize(width = 1280.dp, height = 820.dp)
        )

        fun onCloseApp() {
            // Have a hanging issue, idk what or how, but I spend 5 days, and enough scratching :)
            try {
                appComponent.occtViewModel().close()
            } catch (e: Exception) {
                println("=== cleanup failed: ${e.message} ===")
            }

            Thread {
                Thread.sleep(500)

                try {
                    val pid = ProcessHandle.current().pid()

                    Runtime.getRuntime().exec("taskkill /F /PID $pid")
                } catch (e: Exception) {
                    println("Taskkill failed, falling back to halt: ${e.message}")
                    Runtime.getRuntime().halt(0)
                }
            }.apply {
                isDaemon = true
                start()
            }
            exitApplication()
        }

        DecoratedWindow(
            onCloseRequest = ::onCloseApp,
            state = windowState,
        ) {
            TitleBar { state ->
                Text("Datum")

                IconButton(onClick = { nucleusWindow.setMinimized(true) }) { /* minimize icon */ }
                IconButton(onClick = { nucleusWindow.setMaximized(!state.isMaximized) }) { /* maximize icon */ }
                IconButton(onClick = ::onCloseApp) { /* close icon */ }
            }
            App()
        }
    }
}