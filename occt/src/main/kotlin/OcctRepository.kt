import session.OcctInspectionSession
import javax.inject.Inject

class OcctRepository @Inject constructor() {

    private val sessionManager = OcctSessionManager()

    private fun getSession(): OcctInspectionSession = sessionManager.get()
    private fun closeSession() {}
}