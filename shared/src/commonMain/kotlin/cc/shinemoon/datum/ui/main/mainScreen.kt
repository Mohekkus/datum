package cc.shinemoon.datum.ui.main

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cc.shinemoon.datum.model.raw.preset.PresetModel
import cc.shinemoon.datum.viewmodel.PresetViewModel
import cc.shinemoon.occt.model.OcctInspectionData

@Composable
fun MainScreen(
    data: OcctInspectionData,
    onClear: () -> Unit,
) {
    var barToggled: Boolean by remember { mutableStateOf(false) }
    val viewmodel = remember { PresetViewModel(data) }

    val preset by viewmodel.preset.collectAsState()
    val evaluation by viewmodel.evaluation.collectAsState()

    val listener = object : InspectionInterface {
        override fun onClear() = onClear()

        override fun onToggleSidebar() {
            barToggled = !barToggled
        }

        override fun onPresetLoaded(vPreset: PresetModel) {
            viewmodel.updatePreset(vPreset)
            viewmodel.evaluate()
        }
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
                onPresetLoaded = listener::onPresetLoaded,
                onPresetUpdated = { updated ->
                    viewmodel.updatePreset(updated)
                    viewmodel.evaluate()
                },
            )
        }
    }
}
