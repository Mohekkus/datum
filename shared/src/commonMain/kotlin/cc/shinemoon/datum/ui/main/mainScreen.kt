package cc.shinemoon.datum.ui.main

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cc.shinemoon.datum.ui.TextInputDialog
import cc.shinemoon.datum.di.presetViewModelFactory
import cc.shinemoon.datum.viewmodel.PresetViewModel
import cc.shinemoon.datum.model.preset.PresetModel
import cc.shinemoon.datum.model.occt.OcctInspectionData

@Composable
fun MainScreen(
    data: OcctInspectionData,
    onClear: () -> Unit,
) {
    var barToggled: Boolean by remember { mutableStateOf(false) }
    var savingPresets: Boolean by remember { mutableStateOf(false) }

    val viewmodel: PresetViewModel = remember(data) {
        presetViewModelFactory.create(data)
    }

    val preset by viewmodel.preset.collectAsState()
    val evaluation by viewmodel.evaluation.collectAsState()
    val savedPresetsName by viewmodel.savedPresetsName.collectAsState()

    LaunchedEffect(viewmodel) {
        viewmodel.loadAllPresetsName()
    }

    val listener = object : InspectionInterface {
        override fun onClear() = onClear()

        override fun onToggleSidebar() {
            barToggled = !barToggled
        }

        override fun onPresetModified(vPreset: PresetModel) {
            viewmodel.updatePreset(vPreset)
            viewmodel.evaluate()
        }

        override fun savedPresetList(): List<String> {
            return savedPresetsName
        }

        override fun onLoadPreset(name: String) {
            viewmodel.loadPreset(name)
        }

        override fun onDeletePreset(name: String) {
            viewmodel.deletePreset(name)
        }
    }

    if (savingPresets) {
        TextInputDialog(
            onConfirm = { name ->
                viewmodel.apply {
                    checkNameDuplicate(name) {
                        if (!it)
                            savePreset(name, preset)
                    }
                }
                savingPresets = false
            },
            onDismissRequest = {
                savingPresets = false
            }
        )
    }

    Row(
        modifier = Modifier
            .animateContentSize()
    ) {
        InspectionScreen(
            data = data,
            evaluation = evaluation,
            modifier = Modifier.weight(1f),
            listener,
        )
        if (!barToggled) return@Row
        Box(
            Modifier
                .weight(.5f),
        ) {
            PresetScreen(
                preset = preset,
                evaluation = evaluation,
                presetInterface = object : PresetInterface {
                    override fun onPresetUpdate(presetModel: PresetModel) {
                        viewmodel.updatePreset(presetModel)
                        viewmodel.evaluate()
                    }

                    override fun onSavingCurrentPreset() {
                        savingPresets = true
                    }
                }
            )
        }
    }
}
