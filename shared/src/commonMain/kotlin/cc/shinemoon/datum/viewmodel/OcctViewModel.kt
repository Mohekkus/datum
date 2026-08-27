package cc.shinemoon.datum.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cc.shinemoon.datum.uistate.AppUiState
import cc.shinemoon.occt.OcctDllResolver
import cc.shinemoon.occt.OcctInspectionSession
import cc.shinemoon.occt.model.OcctInspectionData
import cc.shinemoon.occt.toStructuredModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Path

class OcctViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    fun inspect(file: File) {
        if (_uiState.value.isLoading) return
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            _uiState.update { current ->
                runCatching { inspectFile(file) }.fold(
                    onSuccess = { data -> current.copy(isLoading = false, inspectionData = data) },
                    onFailure = { cause ->
                        val message = when (cause) {
                            is OcctInspectionSession.OcctDataException -> "This STEP file produced corrupted inspection data and can't be displayed safely."
                            else -> cause.message ?: "Failed to inspect ${file.name}"
                        }
                        current.copy(isLoading = false, error = message)
                    },
                )
            }
        }
    }

    fun clearResults() {
        _uiState.value = AppUiState()
    }

    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }

    private suspend fun inspectFile(file: File): OcctInspectionData = withContext(Dispatchers.Default) {
        OcctInspectionSession(OcctDllResolver.resolve()).use { session ->
            session.inspect(Path.of(file.absolutePath)).toStructuredModel()
        }
    }
}
