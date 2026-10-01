package session

import model.RawOcctModel
import java.nio.file.Path
import javax.inject.Inject
import javax.inject.Singleton

class OcctSessionManager internal constructor(
    private val session: OcctInspectionSession
) {

    fun inspect(path: Path): RawOcctModel {
        return session.inspect(path)
    }

    fun close() {
        println("[close] OcctSessionManager.close()")
        session.close()
    }
}