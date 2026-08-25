package cc.shinemoon.datum.uistate

import cc.shinemoon.occt.model.OcctInspectionData

data class AppUiState(
    var rawInspectionData: OcctInspectionData? = null,
    var presetRules: String = "",
    var screenStatus: AppScreenStatus = AppScreenStatus.IDLE
)
