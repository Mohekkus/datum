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
import kotlin.system.exitProcess

fun main() {
    nucleusApplication(backend = NucleusBackend.Tao) {
        val appComponent = DaggerAppComponent.create()
        initDependencies(appComponent, appComponent.presetViewModelFactory())

        val windowState = rememberWindowState(
            size = DpSize(width = 1280.dp, height = 820.dp)
        )

        fun onCloseApp() {
            try {
                appComponent.occtViewModel().close()
            } catch (e: Exception) {
                println("OCCT cleanup warning: ${e.message}")
            }

            try {
                appComponent.databaseInitializer().close()
            } catch (e: Exception) {
                println("Database cleanup warning: ${e.message}")
            }

            try {
                exitApplication()
            } finally {
                exitProcess(0)
            }
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
