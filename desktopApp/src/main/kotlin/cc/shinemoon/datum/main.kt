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
import kotlin.system.exitProcess
import session.OcctLifecycleManager

fun main() {
    FileKit.init(appId = "cc.shinemoon.datum")

    nucleusApplication(backend = NucleusBackend.Tao) {
        val appComponent = DaggerAppComponent.create()
        initDependencies(appComponent, appComponent.presetViewModelFactory())

        val windowState = rememberWindowState(
            size = DpSize(width = 1280.dp, height = 820.dp)
        )

        /**
            Known issue: calling exitProcess(0)/System.exit(0) here hangs the JVM indefinitely
            on Windows, with no deadlock or contested lock (confirmed via WinDbg thread dump —
            every thread is cleanly parked; the original JVM main thread's launcher wait
            (jli!CallJavaMainInNewThread) just never gets satisfied). Root cause is unresolved
            and likely sits in the JDK's native launcher/Tao's windowing teardown interaction,
            not application code. DB/OCCT close() and Window.dispose() calls below are correct
            and kept, but do not fix the hang on their own — they were ruled out individually.
            Workaround: skip graceful exitProcess and hard-kill the process instead.
        **/
        fun onCloseApp() {
            try {
                OcctLifecycleManager.cleanup()
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

            try {
                exitApplication()
            } finally {
//                Thread {
//                    try {
//                        val pid = ProcessHandle.current().pid()
//                        Runtime.getRuntime().exec("taskkill /F /PID $pid")
//                    } catch (e: Exception) {
//                        Runtime.getRuntime().halt(0)
//                    }
//                }.apply { isDaemon = true }.start()
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
