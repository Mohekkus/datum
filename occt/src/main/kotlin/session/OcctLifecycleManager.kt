package session

import java.io.Closeable
import java.util.concurrent.ConcurrentHashMap

object OcctLifecycleManager {
    private val activeSessions = ConcurrentHashMap.newKeySet<Closeable>()

    fun register(session: Closeable) {
        activeSessions.add(session)
    }

    fun unregister(session: Closeable) {
        activeSessions.remove(session)
    }

    fun cleanup() {
        activeSessions.forEach { session ->
            try {
                session.close()
            } catch (e: Exception) {
                println("OcctLifecycleManager: Error closing session during cleanup: ${e.message}")
            }
        }
        activeSessions.clear()
    }

    // Provider for OcctSessionManager to be used by the Repository
    val sessionProvider: () -> OcctSessionManager = {
        // We need an OcctInspectionSession to create OcctSessionManager.
        // Since OcctInspectionSession requires OcctDllResolver,
        // we must resolve it here or pass it in.
        // Given the current architecture, let's use a bridge to get the resolver.
        val resolver = bridge.OcctDllResolver()
        val session = OcctInspectionSession(resolver)
        OcctSessionManager(session)
    }
}
