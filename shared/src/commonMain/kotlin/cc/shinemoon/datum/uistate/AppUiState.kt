package cc.shinemoon.datum.uistate

import cc.shinemoon.occt.model.sanitize.OcctInspectionData

data class AppUiState(
    val inspectionData: OcctInspectionData? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)
