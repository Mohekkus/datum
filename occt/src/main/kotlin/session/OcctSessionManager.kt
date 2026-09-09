package session

import OcctDllResolver
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class OcctSessionManager @Inject constructor(
    private val dllResolver: OcctDllResolver
) {

    private var session: OcctInspectionSession? = null

    private fun initialize(): OcctSessionManager {
        session = OcctInspectionSession(dllResolver.resolve())
        return this
    }

    fun close() {
        session?.close()
    }

    fun get(): OcctInspectionSession = when {
            session != null && session?.isOpen == true -> session!!
            else -> {
                initialize()
                session!!
            }
        }
}