package cc.shinemoon.datum.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import cc.shinemoon.datum.uistate.AppUiState
import cc.shinemoon.occt.model.OcctInspectionData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class OcctViewModel: ViewModel() {
    val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    fun addInspectionData(rawInspectionData: OcctInspectionData) {
        _uiState.value = _uiState.value.copy(rawInspectionData = rawInspectionData)
    }

    fun getInspectionData() = _uiState.value.rawInspectionData
    fun purgeInspectionData() { _uiState.value.rawInspectionData = null }
}