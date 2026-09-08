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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cc.shinemoon.datum.ui.MetricStatusChip
import cc.shinemoon.datum.ui.ReusableRowNumberField
import cc.shinemoon.datumabase.model.data.TopologyRules
import cc.shinemoon.datumabase.model.preset.PresetEvaluation
import cc.shinemoon.datumabase.model.utility.RuleGroup
import compose.icons.FeatherIcons
import compose.icons.feathericons.Layers

@Composable
fun TopologyRulesScreen(
    rules: TopologyRules,
    onRulesChanged: (TopologyRules) -> Unit,
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
            Icon(
                FeatherIcons.Layers,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "Topology",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.weight(1f))
            evaluation?.groupStatus(RuleGroup.TOPOLOGY)?.let {
                MetricStatusChip(it)
            }
        }

        if (expanded) {
            Spacer(Modifier.height(8.dp))

            ReusableRowSwitch(
                "BRep Validation",
                rules.requireValidBRep,
            ) { checked ->
                onRulesChanged(rules.copy(requireValidBRep = checked))
            }

            ReusableRowSwitch(
                "Closed Surface",
                rules.requireClosedSolid,
            ) { checked ->
                onRulesChanged(rules.copy(requireClosedSolid = checked))
            }

            ReusableRowNumberField("Max Open Boundary Edges", rules.maxOpenBoundaryEdges?.toDouble(), unit = "") {
                onRulesChanged(rules.copy(maxOpenBoundaryEdges = it?.toInt()))
            }
            ReusableRowNumberField("Max Non-Manifold Edges", rules.maxNonManifoldEdges?.toDouble(), unit = "") {
                onRulesChanged(rules.copy(maxNonManifoldEdges = it?.toInt()))
            }
            ReusableRowNumberField("Min Solids", rules.minSolids?.toDouble(), unit = "") {
                onRulesChanged(rules.copy(minSolids = it?.toInt()))
            }
            ReusableRowNumberField("Max Solids", rules.maxSolids?.toDouble(), unit = "") {
                onRulesChanged(rules.copy(maxSolids = it?.toInt()))
            }
            ReusableRowNumberField("Min Faces", rules.minFaces?.toDouble(), unit = "") {
                onRulesChanged(rules.copy(minFaces = it?.toInt()))
            }
            ReusableRowNumberField("Max Faces", rules.maxFaces?.toDouble(), unit = "") {
                onRulesChanged(rules.copy(maxFaces = it?.toInt()))
            }
        }
    }
}

@Composable
fun ReusableRowSwitch(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        Modifier
            .clickable {
                onCheckedChange(!checked)
            }
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            title,
            style = MaterialTheme.typography.labelMedium
        )

        Spacer(Modifier.weight(1f))

        Switch(
            modifier = Modifier.scale(.65f),
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}
