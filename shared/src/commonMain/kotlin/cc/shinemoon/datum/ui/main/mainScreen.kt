package cc.shinemoon.datum.ui.main

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cc.shinemoon.datum.di.presetViewModelFactory
import cc.shinemoon.datum.model.occt.OcctInspectionData
import cc.shinemoon.datum.model.preset.PresetModel
import cc.shinemoon.datum.ui.PresetConfirmationAction
import cc.shinemoon.datum.ui.PresetConfirmationDialog
import cc.shinemoon.datum.ui.PresetConfirmationModel
import cc.shinemoon.datum.ui.TextInputDialog
import cc.shinemoon.datum.viewmodel.PresetViewModel

@Composable
fun MainScreen(
    data: OcctInspectionData,
    onClear: () -> Unit,
) {
    var barToggled by remember { mutableStateOf(false) }
    var savingPresets by remember { mutableStateOf(false) }
    var pendingDeleteName by remember { mutableStateOf<String?>(null) }

    val viewmodel: PresetViewModel = remember(data) {
        presetViewModelFactory.create(data)
    }

    val preset by viewmodel.preset.collectAsState()
    val evaluation by viewmodel.evaluation.collectAsState()
    val savedPresetsName by viewmodel.savedPresetsName.collectAsState()
    val presetMessage by viewmodel.presetMessage.collectAsState()

    LaunchedEffect(viewmodel) {
        viewmodel.loadAllPresetsName()
    }

    DisposableEffect(viewmodel) {
        onDispose { viewmodel.close() }
    }

    val listener = object : InspectionInterface {
        override fun onClear() = onClear()

        override fun onToggleSidebar() {
            barToggled = if (preset.name.isEmpty())
                !barToggled
            else
                true
        }

        override fun onPresetModified(vPreset: PresetModel) {
            viewmodel.updatePreset(vPreset)
        }

        override fun savedPresetList(): List<String> {
            return savedPresetsName
        }

        override fun onLoadPreset(name: String) {
            viewmodel.loadPreset(name)
        }

        override fun onDeletePreset(name: String) {
            pendingDeleteName = name
        }
    }

    pendingDeleteName?.let { name ->
        PresetConfirmationDialog(
            model = PresetConfirmationModel(
                title = "Delete preset?",
                subtitle = "Are you sure you want to delete \"$name\"? This action cannot be undone.",
                action = PresetConfirmationAction.NEGATIVE,
                actionButtonString = "Delete"
            ),
            onConfirm = {
                viewmodel.deletePreset(name)
                if (barToggled)
                    barToggled = false
                pendingDeleteName = null
            },
            onDismiss = {
                pendingDeleteName = null
            },
        )
    }

    if (savingPresets) {
        if (savedPresetsName.contains(preset.name)) {
            PresetConfirmationDialog(
                model = PresetConfirmationModel(
                    title = "Overwrite current saved preset?",
                    subtitle = "Are you sure want to update current set preset as ${preset.name}?",
                    action = PresetConfirmationAction.POSITIVE,
                    actionButtonString = "Overwrite"
                ),
                onConfirm = {
                    viewmodel.updateSavedPreset()
                    savingPresets = false
                },
                onDismiss = {
                    savingPresets = false
                },
            )
        } else
            TextInputDialog(
                onConfirm = { name ->
                    viewmodel.savePreset(name, preset)
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
                    }

                    override fun onSavingCurrentPreset() {
                        savingPresets = true
                    }

                    override fun onCloseRequest() {
                        barToggled = false
                    }
                }
            )
        }
    }

    presetMessage?.let { message ->
        PresetConfirmationDialog(
            model = PresetConfirmationModel(
                title = "Preset not saved",
                subtitle = message,
                action = PresetConfirmationAction.NEGATIVE,
                actionButtonString = "OK"
            ),
            onConfirm = viewmodel::clearPresetMessage,
            onDismiss = viewmodel::clearPresetMessage,
        )
    }
}
