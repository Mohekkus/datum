package cc.shinemoon.datum.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cc.shinemoon.datum.utility.preset.PresetEvaluator
import cc.shinemoon.datumabase.getAllPresetNames
import cc.shinemoon.datumabase.getAllPresets
import cc.shinemoon.datumabase.getPresetByName
import cc.shinemoon.datumabase.model.preset.PresetEvaluation
import cc.shinemoon.datumabase.model.preset.PresetModel
import cc.shinemoon.datumabase.saveIntoPresets
import cc.shinemoon.occt.model.OcctInspectionData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PresetViewModel(
    private val data: OcctInspectionData
): ViewModel() {

    private val _preset = MutableStateFlow(PresetModel())
    val preset: StateFlow<PresetModel> = _preset.asStateFlow()

    private val _evaluation = MutableStateFlow<PresetEvaluation?>(null)
    val evaluation: StateFlow<PresetEvaluation?> = _evaluation.asStateFlow()

    private val _savedPresetsName = MutableStateFlow<List<String>>(emptyList())
    val savedPresetsName: StateFlow<List<String>> = _savedPresetsName.asStateFlow()

    fun updatePreset(newPreset: PresetModel) {
        _preset.value = newPreset
    }

    fun evaluate() {
        _evaluation.value = PresetEvaluator.evaluate(_preset.value, data)
    }

    fun loadAllPresetsName() {
        viewModelScope.launch {
            _savedPresetsName.value = getAllPresetNames()
        }
    }

    fun loadPreset(presetName: String) {
        viewModelScope.launch {
            val loadedPreset = getPresetByName(presetName)
            updatePreset(loadedPreset)
        }
    }

    fun savePreset(name: String, presetModel: PresetModel) {
        viewModelScope.launch {
            saveIntoPresets(name, presetModel)
        }
    }

}