package cc.shinemoon.datum.ui.main.rules

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cc.shinemoon.datum.model.raw.ToleranceRules
import cc.shinemoon.datum.model.raw.preset.PresetEvaluation
import cc.shinemoon.datum.model.raw.preset.RuleGroup
import cc.shinemoon.datum.ui.MetricStatusChip
import cc.shinemoon.datum.ui.ReusableRowNumberField
import compose.icons.FeatherIcons
import compose.icons.feathericons.Target

@Composable
fun ToleranceRulesScreen(
    rules: ToleranceRules,
    onRulesChanged: (ToleranceRules) -> Unit,
    evaluation: PresetEvaluation? = null,
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = { expanded = !expanded })
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(FeatherIcons.Target, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(8.dp))
            Text("Tolerance", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            evaluation?.groupStatus(RuleGroup.TOLERANCE)?.let {
                MetricStatusChip(it)
            }
        }

        if (expanded) {
            Spacer(Modifier.height(8.dp))
            ReusableRowNumberField("Max Vertex Tolerance", rules.maxVertexTolerance, "mm") { onRulesChanged(rules.copy(maxVertexTolerance = it)) }
            ReusableRowNumberField("Max Edge Tolerance", rules.maxEdgeTolerance, "mm") { onRulesChanged(rules.copy(maxEdgeTolerance = it)) }
            ReusableRowNumberField("Max Face Tolerance", rules.maxFaceTolerance, "mm") { onRulesChanged(rules.copy(maxFaceTolerance = it)) }
        }
    }
}