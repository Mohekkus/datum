package cc.shinemoon.datum.viewmodel

import androidx.lifecycle.ViewModel
import cc.shinemoon.datum.utility.preset.PresetEvaluator
import cc.shinemoon.datumabase.model.preset.PresetEvaluation
import cc.shinemoon.datumabase.model.preset.PresetModel
import cc.shinemoon.occt.model.OcctInspectionData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PresetViewModel(
    private val data: OcctInspectionData
): ViewModel() {

    private val _preset = MutableStateFlow(PresetModel())
    val preset: StateFlow<PresetModel> = _preset.asStateFlow()

    private val _evaluation = MutableStateFlow<PresetEvaluation?>(null)
    val evaluation: StateFlow<PresetEvaluation?> = _evaluation.asStateFlow()

    fun updatePreset(newPreset: PresetModel) {
        _preset.value = newPreset
    }

    fun evaluate() {
        _evaluation.value = PresetEvaluator.evaluate(_preset.value, data)
    }

}