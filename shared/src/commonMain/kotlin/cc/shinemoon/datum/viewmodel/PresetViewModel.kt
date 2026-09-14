package cc.shinemoon.datum.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cc.shinemoon.datum.model.occt.OcctInspectionData
import cc.shinemoon.datum.model.preset.PresetModel
import cc.shinemoon.datum.usecase.DatabaseUseCase
import cc.shinemoon.datum.utility.preset.PresetEvaluation
import cc.shinemoon.datum.utility.preset.PresetEvaluator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class PresetViewModel(
    private val useCase: DatabaseUseCase,
    private val data: OcctInspectionData,
) : ViewModel() {

    private val _preset = MutableStateFlow(PresetModel())
    val preset: StateFlow<PresetModel> = _preset.asStateFlow()

    private val _evaluation = MutableStateFlow<PresetEvaluation?>(null)
    val evaluation: StateFlow<PresetEvaluation?> = _evaluation.asStateFlow()

    private val _savedPresetsName = MutableStateFlow<List<String>>(emptyList())
    val savedPresetsName: StateFlow<List<String>> = _savedPresetsName.asStateFlow()

    private val _presetMessage = MutableStateFlow<String?>(null)
    val presetMessage: StateFlow<String?> = _presetMessage.asStateFlow()

    fun updatePreset(newPreset: PresetModel) {
        _preset.value = newPreset
        evaluate()
    }

    fun evaluate() {
        _evaluation.value = PresetEvaluator.evaluate(_preset.value, data)
    }

    fun loadAllPresetsName() {
        viewModelScope.launch {
            _savedPresetsName.value = useCase.getAllNames()
        }
    }

    fun loadPreset(name: String) {
        viewModelScope.launch {
            useCase.get(name)?.let { loaded ->
                updatePreset(loaded.copy(name = name))
            }
        }
    }

    fun savePreset(name: String, presetModel: PresetModel = _preset.value) {
        viewModelScope.launch {
            val presetName = name.trim()
            if (presetName.isBlank()) {
                _presetMessage.value = "Preset name is required."
                return@launch
            }

            if (useCase.get(presetName) != null) {
                _presetMessage.value = "Preset \"$presetName\" already exists."
                return@launch
            }

            val savedPreset = presetModel.copy(name = presetName)
            useCase.add(presetName, savedPreset)
            _savedPresetsName.value = useCase.getAllNames()
            updatePreset(savedPreset)
            _presetMessage.value = null
        }
    }

    fun updateSavedPreset(presetModel: PresetModel = _preset.value) {
        val presetName = presetModel.name
        viewModelScope.launch {
            if (presetName.isBlank()) {
                _presetMessage.value = "Preset name is required before updating."
                return@launch
            }

            val updated = useCase.update(presetName, presetModel)
            if (!updated) {
                _presetMessage.value = "Preset \"$presetName\" no longer exists."
                _savedPresetsName.value = useCase.getAllNames()
                return@launch
            }

            _savedPresetsName.value = useCase.getAllNames()
            updatePreset(presetModel)
            _presetMessage.value = null
        }
    }

    fun deletePreset(name: String) {
        viewModelScope.launch {
            useCase.delete(name)
            _savedPresetsName.value = useCase.getAllNames()
            updatePreset(PresetModel())
        }
    }

    fun clearPresetMessage() {
        _presetMessage.value = null
    }

    fun close() {
        viewModelScope.launch { close() }
    }
}

class PresetViewModelFactory @Inject constructor(
    private val useCase: DatabaseUseCase,
) {
    fun create(data: OcctInspectionData): PresetViewModel =
        PresetViewModel(useCase, data)
}
