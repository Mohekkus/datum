package cc.shinemoon.datum.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cc.shinemoon.datum.uistate.AppUiState
import cc.shinemoon.datum.model.occt.OcctInspectionData
import cc.shinemoon.datum.usecase.OcctUseCase
import contracts.OcctViewModelContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class OcctViewModel @Inject constructor(
    private val occtUseCase: OcctUseCase
) : ViewModel(), OcctViewModelContract {

    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    override fun inspect(file: File) {
        if (_uiState.value.isLoading) return
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            _uiState.update { current ->
                runCatching { inspectFile(file) }.fold(
                    onSuccess = { data -> current.copy(isLoading = false, inspectionData = data) },
                    onFailure = { cause ->
                        println(cause.stackTrace.contentToString())
                        current.copy(isLoading = false, error = cause.message ?: cause.toString())
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
        occtUseCase.inspect(file)
    }
}
