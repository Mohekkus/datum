package cc.shinemoon.datum.occt

import cc.shinemoon.datum.model.occt.OcctInspectionData
import cc.shinemoon.datum.usecase.OcctUseCase
import cc.shinemoon.datum.utility.toStructuredModel
import session.OcctSessionManager
import java.io.File
import java.nio.file.Path
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OcctRepository @Inject constructor(
    private val sessionManager: OcctSessionManager
): OcctUseCase {

    override fun inspect(file: File): OcctInspectionData =
        sessionManager.inspect(file.toPath()).toStructuredModel()

    fun close() {
        sessionManager.close()
    }
}