package cc.shinemoon.datum.uistate

import cc.shinemoon.datum.model.occt.OcctInspectionData

data class AppUiState(
    val inspectionData: OcctInspectionData? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)
