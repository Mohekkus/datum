package cc.shinemoon.datum.usecase

import cc.shinemoon.datum.model.occt.OcctInspectionData
import java.io.File

interface OcctUseCase {
    fun inspect(file: File): OcctInspectionData
}
