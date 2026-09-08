package cc.shinemoon.datum.ui.main

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import cc.shinemoon.datum.ui.main.rules.GeometryRulesScreen
import cc.shinemoon.datum.ui.main.rules.MassPropertyRulesScreen
import cc.shinemoon.datum.ui.main.rules.ToleranceRulesScreen
import cc.shinemoon.datum.ui.main.rules.TopologyRulesScreen
import cc.shinemoon.datumabase.model.preset.PresetEvaluation
import cc.shinemoon.datumabase.model.preset.PresetModel
import cc.shinemoon.datumabase.model.utility.MetricStatus
import compose.icons.FeatherIcons
import compose.icons.feathericons.Check
import compose.icons.feathericons.XCircle
import java.awt.EventQueue
import java.awt.FileDialog
import java.awt.Frame
import java.io.FilenameFilter

@Composable
fun PresetScreen(
    preset: PresetModel?,
    evaluation: PresetEvaluation?,
    onPresetLoaded: (PresetModel) -> Unit,
    onPresetUpdated: (PresetModel) -> Unit,
) {
    var parseError by remember { mutableStateOf<String?>(null) }
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .animateContentSize()
            .fillMaxSize()
            .padding(start = 16.dp, top = 16.dp, bottom = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Rules", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(16.dp))

            // Overall verdict chip
            evaluation?.let { result ->
                Spacer(Modifier.height(12.dp))
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
                            text = "${if (passed) "PASS" else "FAIL"} · ${result.passedCount}/${result.checks.size} checks",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (passed) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
                if (result.checks.isNotEmpty()) {
                    Spacer(Modifier.height(6.dp))
                    SegmentedProgressBar(
                        passed = result.passedCount,
                        failed = result.checks.size - result.passedCount,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                    )
                }

                Spacer(Modifier.height(16.dp))
            }

            parseError?.let { message ->
                Spacer(Modifier.height(8.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // Rule configuration
            preset?.let { active ->
                Spacer(Modifier.height(4.dp))
                TopologyRulesScreen(
                    rules = active.topologyRules,
                    onRulesChanged = { updated -> onPresetUpdated(active.copy(topologyRules = updated)) },
                    evaluation = evaluation,
                )
                Spacer(Modifier.height(12.dp))
                MassPropertyRulesScreen(
                    rules = active.massPropertyRules,
                    onRulesChanged = { updated -> onPresetUpdated(active.copy(massPropertyRules = updated)) },
                    evaluation = evaluation,
                )
                Spacer(Modifier.height(12.dp))
                ToleranceRulesScreen(
                    rules = active.toleranceRules,
                    onRulesChanged = { updated -> onPresetUpdated(active.copy(toleranceRules = updated)) },
                    evaluation = evaluation,
                )
                Spacer(Modifier.height(12.dp))
                GeometryRulesScreen(
                    rules = active.geometryRules,
                    onRulesChanged = { updated -> onPresetUpdated(active.copy(geometryRules = updated)) },
                    evaluation = evaluation,
                )
            }
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
