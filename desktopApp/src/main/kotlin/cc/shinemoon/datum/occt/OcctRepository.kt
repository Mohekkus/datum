package cc.shinemoon.datum.occt

import cc.shinemoon.datum.model.occt.OcctInspectionData
import cc.shinemoon.datum.utility.toStructuredModel
import session.OcctSessionManager
import java.nio.file.Path
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OcctRepository @Inject constructor(
    private val sessionManager: OcctSessionManager
) {

    fun inspect(filePath: Path): OcctInspectionData {
        return sessionManager.inspect(filePath).toStructuredModel()
    }

    fun close() {
        sessionManager.close()
    }
}