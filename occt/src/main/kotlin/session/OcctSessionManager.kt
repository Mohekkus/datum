package session

import model.RawOcctModel
import java.nio.file.Path
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OcctSessionManager @Inject internal constructor(
    private val session: OcctInspectionSession
) {

    fun inspect(path: Path): RawOcctModel {
        return session.inspect(path)
    }

    fun close() {
        session.close()
    }
}