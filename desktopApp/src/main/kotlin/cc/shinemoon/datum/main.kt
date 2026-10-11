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
import io.github.vinceglb.filekit.FileKit
import java.io.File

fun main(args: Array<String>) {
    FileKit.init(appId = "cc.shinemoon.datum")

    val pendingStepFile = resolveStepFileFromArgs(args)

    nucleusApplication(args, backend = NucleusBackend.Tao) {
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
                java.awt.Window.getWindows().forEach { it.dispose() }
            } catch (e: Exception) {
                println("Windows cleanup warning: ${e.message}")
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
            App(initialFile = pendingStepFile)
        }
    }
}

private fun resolveStepFileFromArgs(args: Array<String>): File? =
    args
        .map { it.trim().trimQuotes() }
        .filter { it.isNotBlank() }
        .filter { it.isNotEmpty() && it[0].isLetterOrDigit() }
        .firstNotNullOfOrNull { path ->
            val file = File(path)
            if (file.isFile && (path.endsWith(".stp", ignoreCase = true) || path.endsWith(".step", ignoreCase = true))) file else null
        }

private fun String.trimQuotes(): String =
    if (startsWith('"') && endsWith('"') && length >= 2) this.substring(1, length - 1) else this
