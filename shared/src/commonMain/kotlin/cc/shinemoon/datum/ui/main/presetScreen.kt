package cc.shinemoon.datum.ui.main

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cc.shinemoon.datum.ui.main.rules.GeometryRulesScreen
import cc.shinemoon.datum.ui.main.rules.MassPropertyRulesScreen
import cc.shinemoon.datum.ui.main.rules.ToleranceRulesScreen
import cc.shinemoon.datum.ui.main.rules.TopologyRulesScreen
import cc.shinemoon.datum.utility.preset.PresetEvaluation
import cc.shinemoon.datum.model.preset.PresetModel
import cc.shinemoon.datum.types.preset.MetricStatus
import compose.icons.FeatherIcons
import compose.icons.feathericons.Check
import compose.icons.feathericons.Minimize
import compose.icons.feathericons.Save
import compose.icons.feathericons.XCircle

interface PresetInterface {
    fun onPresetUpdate(presetModel: PresetModel)
    fun onSavingCurrentPreset()
    fun onCloseRequest()
}

@Composable
fun PresetScreen(
    preset: PresetModel?,
    evaluation: PresetEvaluation?,
    presetInterface: PresetInterface
) {
    var parseError by remember { mutableStateOf<String?>(null) }

    Card(
        modifier = Modifier
            .animateContentSize()
            .fillMaxSize()
            .padding(start = 16.dp)
            .animateContentSize(),
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            PresetHeader(
                presetName = preset?.name.orEmpty(),
                evaluation = evaluation,
                canClose = preset?.name?.isNotBlank() == true,
                canSave = evaluation?.checks?.isNotEmpty() == true,
                onCloseRequest = presetInterface::onCloseRequest,
                onSaveRequest = presetInterface::onSavingCurrentPreset,
            )

            parseError?.let { message ->
                Spacer(Modifier.height(8.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            preset?.let { active ->
                Spacer(Modifier.height(4.dp))
                TopologyRulesScreen(
                    rules = active.topologyRules,
                    onRulesChanged = { updated -> presetInterface.onPresetUpdate(active.copy(topologyRules = updated)) },
                    evaluation = evaluation,
                )
                Spacer(Modifier.height(12.dp))
                MassPropertyRulesScreen(
                    rules = active.massPropertyRules,
                    onRulesChanged = { updated -> presetInterface.onPresetUpdate(active.copy(massPropertyRules = updated)) },
                    evaluation = evaluation,
                )
                Spacer(Modifier.height(12.dp))
                ToleranceRulesScreen(
                    rules = active.toleranceRules,
                    onRulesChanged = { updated -> presetInterface.onPresetUpdate(active.copy(toleranceRules = updated)) },
                    evaluation = evaluation,
                )
                Spacer(Modifier.height(12.dp))
                GeometryRulesScreen(
                    rules = active.geometryRules,
                    onRulesChanged = { updated -> presetInterface.onPresetUpdate(active.copy(geometryRules = updated)) },
                    evaluation = evaluation,
                )
            }
        }
    }
}

@Composable
private fun PresetHeader(
    presetName: String,
    evaluation: PresetEvaluation?,
    canClose: Boolean,
    canSave: Boolean,
    onCloseRequest: () -> Unit,
    onSaveRequest: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (canClose) {
                IconButton(
                    modifier = Modifier.size(36.dp),
                    onClick = onCloseRequest
                ) {
                    Icon(
                        imageVector = FeatherIcons.Minimize,
                        contentDescription = "Minimize preset panel",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Rules",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (presetName.isNotBlank()) {
                    Text(
                        text = presetName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            evaluation?.takeIf { it.checks.isNotEmpty() }?.let { result ->
                EvaluationStatusChip(result)
            }

            if (canSave) {
                FilledTonalIconButton(
                    modifier = Modifier.size(36.dp),
                    onClick = onSaveRequest
                ) {
                    Icon(
                        imageVector = FeatherIcons.Save,
                        contentDescription = "Save rules",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        evaluation?.takeIf { it.checks.isNotEmpty() }?.let { result ->
            SegmentedProgressBar(
                passed = result.passedCount,
                failed = result.checks.size - result.passedCount,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
            )
        }
    }
}

@Composable
private fun EvaluationStatusChip(result: PresetEvaluation) {
    val passed = result.overall == MetricStatus.PASS
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (passed) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (passed) FeatherIcons.Check else FeatherIcons.XCircle,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = if (passed) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = "${if (passed) "PASS" else "FAIL"} · ${result.passedCount}/${result.checks.size}",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = if (passed) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

@Composable
private fun SegmentedProgressBar(
    passed: Int,
    failed: Int,
    modifier: Modifier = Modifier,
) {
    val total = passed + failed
    if (total <= 0) return
    Row(modifier = modifier) {
        if (passed > 0) {
            Box(
                modifier = Modifier
                    .weight(passed.toFloat())
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
        if (failed > 0) {
            Box(
                modifier = Modifier
                    .weight(failed.toFloat())
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.error)
            )
        }
    }
}
